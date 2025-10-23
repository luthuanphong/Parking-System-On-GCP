package com.gcp.practise.parking.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingRequestPubsubMessage {
    private Integer spotId;
    private Integer userId;
    private String username;
    private Integer vehicleId;
}
