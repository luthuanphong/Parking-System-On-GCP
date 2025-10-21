package com.gcp.practise.parking.dtos.responses;

import com.gcp.practise.parking.enums.ReservationStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReservationResponse {
    private Integer spotId;
    private Integer userId;
    private Integer vehicleId;
    private String reservedForDate;
    private ReservationStatus reservationStatus;
}