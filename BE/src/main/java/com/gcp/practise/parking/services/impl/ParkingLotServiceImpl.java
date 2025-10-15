package com.gcp.practise.parking.services.impl;

import com.gcp.practise.parking.dtos.responses.ParkingSpotResponse;
import com.gcp.practise.parking.dtos.responses.ReservationResponse;
import com.gcp.practise.parking.entities.ParkingSpotEntity;
import com.gcp.practise.parking.entities.ReservationEntity;
import com.gcp.practise.parking.repositories.ParkingSpotRepository;
import com.gcp.practise.parking.repositories.ReservationRepository;
import com.gcp.practise.parking.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Override
    @Cacheable(value = "currentReservations", key = "#root.method.name")
    public List<ReservationResponse> getCurrentReservations() {
        LocalDate targetDate = getTargetDate();
        
        List<ReservationEntity> currentReservations = reservationRepository.findByReservedForDate(targetDate);
        
        return currentReservations.stream()
            .map(this::mapToReservationResponse)
            .collect(Collectors.toList());
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

    @Override
    @Cacheable(value = "parkingSpot", key = "#spotId")
    public ParkingSpotResponse findParkingSpotById(Long spotId) {
        // Find the parking spot
        ParkingSpotEntity spot = parkingSpotRepository.findById(spotId.intValue())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Parking spot not found"));

        // Check if the spot is reserved for the target date
        LocalDate targetDate = getTargetDate();
        boolean isReserved = reservationRepository.existsBySpotIdAndReservedForDate(spot.getId(), targetDate);

        // Map to response
        return ParkingSpotResponse.builder()
            .id(Long.valueOf(spot.getId()))
            .name(spot.getName())
            .isReserved(isReserved)
            .build();
    }

    @Override
    @Cacheable(value = "parkingSpots", key = "#root.method.name")
    public List<ParkingSpotResponse> getAllSpots() {
        // Get all parking spots
        List<ParkingSpotEntity> spots = parkingSpotRepository.findAll();
        
        // Get target date based on current time
        LocalDate targetDate = getTargetDate();
        
        // Get all reserved spot IDs for the target date
        Set<Integer> reservedSpotIds = reservationRepository.findByReservedForDate(targetDate)
            .stream()
            .map(reservation -> reservation.getSpot().getId())
            .collect(Collectors.toSet());

        // Map to response DTOs with reservation status
        return spots.stream()
            .map(spot -> ParkingSpotResponse.builder()
                .id(Long.valueOf(spot.getId()))
                .name(spot.getName())
                .isReserved(reservedSpotIds.contains(spot.getId()))
                .build())
            .collect(Collectors.toList());
    }
}