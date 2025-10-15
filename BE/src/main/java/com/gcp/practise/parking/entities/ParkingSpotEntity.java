package com.gcp.practise.parking.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "parking_spots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParkingSpotEntity {

    @Id
    private Integer id; // IDs provided externally (1..80)

    private String name;
}
