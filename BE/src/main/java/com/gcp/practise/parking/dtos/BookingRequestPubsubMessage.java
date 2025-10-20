package com.gcp.practise.parking.dtos;

import lombok.Data;

@Data
public class BookingRequestPubsubMessage {
    private Integer spotId;
    private Integer userId;
    private String username;
    private Integer vehicleId;
}
