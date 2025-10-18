package com.gcp.practise.parking.services;

import com.gcp.practise.parking.dtos.responses.ParkingSpotResponse;
import com.gcp.practise.parking.dtos.responses.ReservationResponse;


import java.util.List;

public interface ParkingLotService {
    List<ReservationResponse> getCurrentReservations();
    ParkingSpotResponse findParkingSpotById(Integer spotId);
    List<ParkingSpotResponse> getAllSpots();
}