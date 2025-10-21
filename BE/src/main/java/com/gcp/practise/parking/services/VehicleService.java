package com.gcp.practise.parking.services;

import com.gcp.practise.parking.dtos.responses.VehicleResponse;

import java.util.List;

public interface VehicleService {
    List<VehicleResponse> getAllVehicles();
}