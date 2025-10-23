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
public class ParkingSpotEntity implements java.io.Serializable {

    private static final long serialVersionUID = 1l;

    @Id
    private Integer id; // IDs provided externally (1..80)

    private String name;
}