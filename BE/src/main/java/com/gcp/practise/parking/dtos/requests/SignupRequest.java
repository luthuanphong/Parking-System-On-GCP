package com.gcp.practise.parking.dtos.requests;

import lombok.Data;

@Data
public class SignupRequest {
    private String username;
    private String password;
    private String licensePlate;
}