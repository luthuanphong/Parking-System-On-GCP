package com.gcp.practise.parking.services;

import com.gcp.practise.parking.dtos.requests.BookParkingSpotRequest;
import com.gcp.practise.parking.dtos.responses.ReservationResponse;
import com.gcp.practise.parking.security.CustomUserDetails;

public interface BookingService {
    ReservationResponse bookParkingSpot(BookParkingSpotRequest request, CustomUserDetails userDetails);
}