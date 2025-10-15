package com.gcp.practise.parking.services.impl;

import com.gcp.practise.parking.dtos.requests.BookParkingSpotRequest;
import com.gcp.practise.parking.dtos.responses.ParkingSpotResponse;
import com.gcp.practise.parking.dtos.responses.ReservationResponse;
import com.gcp.practise.parking.entities.ParkingSpotEntity;
import com.gcp.practise.parking.entities.ReservationEntity;
import com.gcp.practise.parking.entities.UserEntity;
import com.gcp.practise.parking.repositories.UserRepository;
import com.gcp.practise.parking.security.CustomUserDetails;
import com.gcp.practise.parking.services.ParkingLotService;
import com.gcp.practise.parking.repositories.ParkingSpotRepository;
import com.gcp.practise.parking.repositories.ReservationRepository;
import com.gcp.practise.parking.services.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
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
    private UserRepository userRepository;

    @Override
    @CacheEvict(cacheNames = {"currentReservations", "parkingSpot", "parkingSpots"}, allEntries = true)
    public ReservationResponse bookParkingSpot(BookParkingSpotRequest request, CustomUserDetails userDetails) {
        LocalDate targetDate = getTargetDate();
        
        UserEntity user = userRepository.findById(userDetails.getUserId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        // Use cached parking spot information if available
        ParkingSpotResponse spotInfo = parkingLotService.findParkingSpotById(request.getSpotId());
        
        // Check if spot exists and is not already reserved
        if (spotInfo == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking spot not found");
        }
        if (spotInfo.isReserved()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Parking spot is already reserved for this date");
        }

        // Get the actual spot entity for persistence
        ParkingSpotEntity spot = parkingSpotRepository.findById(request.getSpotId().intValue())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking spot not found"));
        
        // Create new reservation
        ReservationEntity reservation = ReservationEntity.builder()
            .spot(spot)
            .user(user)
            .reservedForDate(targetDate)
            .createdAt(OffsetDateTime.now())
            .build();
            
        ReservationEntity savedReservation = reservationRepository.save(reservation);
        
        return mapToReservationResponse(savedReservation);
    }
    
    private LocalDate getTargetDate() {
        // Check if current time is after 8 PM (20:00)
        if (LocalTime.now().isAfter(LocalTime.of(20, 0))) {
            return LocalDate.now().plusDays(1); // Use tomorrow's date
        }
        return LocalDate.now(); // Use today's date
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