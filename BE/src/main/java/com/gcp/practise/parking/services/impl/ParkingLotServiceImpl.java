package com.gcp.practise.parking.services.impl;

import com.gcp.practise.parking.common.CacheConfiguration;
import com.gcp.practise.parking.dtos.responses.ParkingSpotResponse;
import com.gcp.practise.parking.dtos.responses.ReservationResponse;
import com.gcp.practise.parking.entities.ParkingSpotEntity;
import com.gcp.practise.parking.entities.ReservationEntity;
import com.gcp.practise.parking.entities.VehicleEntity;
import com.gcp.practise.parking.enums.ReservationStatus;
import com.gcp.practise.parking.repositories.ParkingSpotRepository;
import com.gcp.practise.parking.repositories.ReservationRepository;
import com.gcp.practise.parking.repositories.VehicleRepository;
import com.gcp.practise.parking.services.ParkingLotService;
import com.gcp.practise.parking.utils.DateUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ParkingLotServiceImpl implements ParkingLotService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Override
    public List<ReservationResponse> getCurrentReservations() {
        List<ReservationEntity> currentReservations = reservationRepository.findByReservedForDate(DateUtils.getTargetDate());
        return currentReservations.stream()
            .map(this::mapToReservationResponse)
            .collect(Collectors.toList());
    }
    
   
    private ReservationResponse mapToReservationResponse(ReservationEntity reservation) {
        return ReservationResponse.builder()
            .spotId(reservation.getSpotId())
            .userId(reservation.getUserId())
            .vehicleId(reservation.getVehicleId())
            .reservedForDate(reservation.getReservedForDate().toString())
            .reservationStatus(ReservationStatus.RESERVED)
            .build();
    }

    @Override
    public ParkingSpotResponse findParkingSpotById(Integer spotId) {
        List<ReservationEntity> currentReservations = reservationRepository.findByReservedForDate(DateUtils.getTargetDate());
        boolean isReserved = currentReservations.stream()
            .anyMatch(reservation -> reservation.getSpotId().equals(spotId));

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
        
        // Get current reservations for today
        List<ReservationEntity> currentReservations = reservationRepository.findByReservedForDate(DateUtils.getTargetDate());
        
        // Create a map of spot ID to license plate for reserved spots
        Map<Integer, String> reservedSpotLicensePlates = currentReservations.stream()
            .collect(Collectors.toMap(
                ReservationEntity::getSpotId, // Use spotId field directly
                reservation -> getLicensePlateFromCache(reservation.getVehicleId()), // Get license from cache using vehicleId
                (existing, replacement) -> existing // In case of duplicates, keep the first
            ));
        
        return spots.stream()
            .map(spot -> {
                boolean isReserved = reservedSpotLicensePlates.containsKey(spot.getId());
                String licensePlate = isReserved ? reservedSpotLicensePlates.get(spot.getId()) : null;
                
                return ParkingSpotResponse.builder()
                    .id(spot.getId())
                    .name(spot.getName())
                    .isReserved(isReserved)
                    .reservedLicensePlate(licensePlate)
                    .build();
            })
            .collect(Collectors.toList());
    }
    
    private String getLicensePlateFromCache(Integer vehicleId) {
        var vehicle = vehicleRepository.findByIdOrThrow(vehicleId);
        return vehicle.getPlateNormalized();
    }
}