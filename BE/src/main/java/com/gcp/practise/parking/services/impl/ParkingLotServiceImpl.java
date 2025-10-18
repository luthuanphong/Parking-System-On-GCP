package com.gcp.practise.parking.services.impl;

import com.gcp.practise.parking.common.CacheConfiguration;
import com.gcp.practise.parking.dtos.responses.ParkingSpotResponse;
import com.gcp.practise.parking.dtos.responses.ReservationResponse;
import com.gcp.practise.parking.entities.ParkingSpotEntity;
import com.gcp.practise.parking.entities.ReservationEntity;
import com.gcp.practise.parking.repositories.ParkingSpotRepository;
import com.gcp.practise.parking.repositories.ReservationRepository;
import com.gcp.practise.parking.services.ParkingLotService;

import org.apache.catalina.webresources.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {

    @Autowired
    private ReservationRepository reservationRepository;
    
    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Override
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
    public ParkingSpotResponse findParkingSpotById(Integer spotId) {
        // Find the parking spot
        LocalDate targetDate = getTargetDate();
        List<ReservationEntity> currentReservations = reservationRepository.findByReservedForDate(targetDate);

        boolean isReserved = currentReservations.stream()
            .anyMatch(reservation -> reservation.getSpot().getId().equals(spotId));

        // Map to response
        return ParkingSpotResponse.builder()
            .id(spotId)
            .isReserved(isReserved)
            .build();
    }

    @Override
    public List<ParkingSpotResponse> getAllSpots() {
        // Get all parking spots
        List<ParkingSpotEntity> spots = parkingSpotRepository.findAll();
        return spots.stream()
            .map(spot -> ParkingSpotResponse.builder()
                .id(spot.getId())
                .build())
            .collect(Collectors.toList());
    }
}