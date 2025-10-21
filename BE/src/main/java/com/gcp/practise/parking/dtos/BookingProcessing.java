package com.gcp.practise.parking.dtos;

import java.io.Serializable;

import com.gcp.practise.parking.dtos.requests.BookParkingSpotRequest;
import com.gcp.practise.parking.security.CustomUserDetails;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookingProcessing implements Serializable {
    private BookParkingSpotRequest request;
    private CustomUserDetails userDetails;
    
}
