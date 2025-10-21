package com.gcp.practise.parking.dtos.responses;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VehicleResponse {
    private Integer id;
    private Integer userId;
    private String plateNormalized;
    private String createdAt;
}