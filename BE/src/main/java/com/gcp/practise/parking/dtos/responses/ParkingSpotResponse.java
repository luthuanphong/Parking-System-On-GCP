package com.gcp.practise.parking.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParkingSpotResponse {
    private Long id;
    private String name;
    private boolean isReserved;
}