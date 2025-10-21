package com.gcp.practise.parking.dtos.requests;

import java.io.Serializable;

import lombok.Data;

@Data
public class BookParkingSpotRequest implements Serializable {
    private Integer spotId;
}