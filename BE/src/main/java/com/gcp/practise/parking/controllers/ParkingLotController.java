package com.gcp.practise.parking.controllers;

import com.gcp.practise.parking.dtos.requests.BookParkingSpotRequest;
import com.gcp.practise.parking.dtos.responses.ParkingSpotResponse;
import com.gcp.practise.parking.dtos.responses.ReservationResponse;
import com.gcp.practise.parking.security.CustomUserDetails;
import com.gcp.practise.parking.services.BookingService;
import com.gcp.practise.parking.services.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/parking")
public class ParkingLotController {
    
    @Autowired
    private ParkingLotService parkingLotService;
    
    @Autowired
    private BookingService bookingService;

    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservedSpots() {
        List<ReservationResponse> currentReservations = parkingLotService.getCurrentReservations();
        return ResponseEntity.ok(currentReservations);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> bookParkingSpot(
            @RequestBody BookParkingSpotRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
            
        ReservationResponse reservation = bookingService.bookParkingSpot(request, userDetails);
        return ResponseEntity.ok(reservation);
    }
    
    @GetMapping("/spots")
    public ResponseEntity<List<ParkingSpotResponse>> getAllParkingSpots() {
        List<ParkingSpotResponse> spots = parkingLotService.getAllSpots();
        return ResponseEntity.ok(spots);
    }
}