package com.gcp.practise.parking.services.impl;

import com.gcp.practise.parking.dtos.requests.BookParkingSpotRequest;
import com.gcp.practise.parking.dtos.responses.ParkingSpotResponse;
import com.gcp.practise.parking.dtos.responses.ReservationResponse;
import com.gcp.practise.parking.entities.ParkingSpotEntity;
import com.gcp.practise.parking.entities.ReservationEntity;
import com.gcp.practise.parking.security.CustomUserDetails;
import com.gcp.practise.parking.services.ParkingLotService;
import com.gcp.practise.parking.utils.DateUtils;
import com.gcp.practise.parking.repositories.ParkingSpotRepository;
import com.gcp.practise.parking.repositories.ReservationRepository;
import com.gcp.practise.parking.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private ParkingLotService parkingLotService;

    @Autowired
    private UserDetailServiceImpl userDetailsServiceImpl;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public ReservationResponse bookParkingSpot(BookParkingSpotRequest request, CustomUserDetails userDetails) {
        LocalDate targetDate = DateUtils.getTargetDate();
        
        if (userDetails.getBalanceCents() < 1000) { // Assuming 1000 cents ($10) is the minimum balance required
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Insufficient balance to book a parking spot");
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

        // Create new reservation
        ReservationEntity reservation = ReservationEntity.builder()
            .spotId(spot.getId())
            .userId(userDetails.getUserId())
            .vehicleId(userDetails.getVehicleId())
            .reservedForDate(targetDate)
            .createdAt(OffsetDateTime.now())
            .build();
            
        ReservationEntity savedReservation = reservationRepository.save(reservation);

        userDetails.setBalanceCents(userDetails.getBalanceCents() - 1000);
        userDetailsServiceImpl.updateUserBalance(userDetails);

        ListOperations<String, Object> listOps = redisTemplate.opsForList();
        listOps.leftPush(DateUtils.getTargetDate().toString(), savedReservation);

        return mapToReservationResponse(savedReservation);
    }
    
    private ReservationResponse mapToReservationResponse(ReservationEntity reservation) {
        return ReservationResponse.builder()
            .spotId(Long.valueOf(reservation.getSpot().getId()))
            .spotName(reservation.getSpot().getName())
            .userName(reservation.getUser().getEmail())
            .userEmail(reservation.getUser().getEmail())
            .reservedForDate(reservation.getReservedForDate().toString())
            .build();
    }
}