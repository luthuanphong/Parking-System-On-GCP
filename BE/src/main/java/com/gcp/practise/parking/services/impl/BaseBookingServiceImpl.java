package com.gcp.practise.parking.services.impl;

import com.gcp.practise.parking.common.CacheConfiguration;
import com.gcp.practise.parking.concurent.BookingProcessingQueue;
import com.gcp.practise.parking.dtos.BookingProcessing;
import com.gcp.practise.parking.dtos.requests.BookParkingSpotRequest;
import com.gcp.practise.parking.dtos.responses.ParkingSpotResponse;
import com.gcp.practise.parking.dtos.responses.ReservationResponse;
import com.gcp.practise.parking.entities.ParkingSpotEntity;
import com.gcp.practise.parking.enums.ReservationStatus;
import com.gcp.practise.parking.security.CustomUserDetails;
import com.gcp.practise.parking.services.ParkingLotService;

import lombok.RequiredArgsConstructor;

import com.gcp.practise.parking.repositories.ParkingSpotRepository;
import com.gcp.practise.parking.services.BookingService;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
public abstract class BaseBookingServiceImpl implements BookingService {
    
    private final ParkingSpotRepository parkingSpotRepository;

    private final ParkingLotService parkingLotService;

    private final CacheManager cacheManager;

    @Override
    public ReservationResponse bookParkingSpot(BookParkingSpotRequest request, CustomUserDetails userDetails) {
        BookingProcessing processing = new BookingProcessing(request, userDetails);

        if (userDetails.getBalanceCents() < 1000) { // Assuming 1000 cents ($10) is the minimum balance required
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient balance to book a parking spot");
        }

        Cache cache = cacheManager.getCache(CacheConfiguration.USER_RESERVATION_REQUEST_CACHE_NAME);
        ValueWrapper cachedRequest = cache.putIfAbsent(userDetails.getUsername(), processing);
        if (cachedRequest != null) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "A booking request is already in progress for this user");
        }

        // Use cached parking spot information if available
        ParkingSpotResponse spotInfo = parkingLotService.findParkingSpotById(request.getSpotId());
        
        // Check if spot exists and is not already reserved
        if (spotInfo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking spot not found");
        }
        if (spotInfo.isReserved()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Parking spot is already reserved for this date");
        }

        ParkingSpotEntity spot = parkingSpotRepository.findAll()
        .stream()
        .filter(s -> s.getId().equals(request.getSpotId().intValue()))
        .findFirst()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking spot not found"));

        proceedRequest(processing);

        return ReservationResponse.builder()
            .spotId(spot.getId())
            .reservationStatus(ReservationStatus.RESERVING) // Indicate that the reservation is being processed
            .build();
    }

    protected abstract void proceedRequest(BookingProcessing processing);
}