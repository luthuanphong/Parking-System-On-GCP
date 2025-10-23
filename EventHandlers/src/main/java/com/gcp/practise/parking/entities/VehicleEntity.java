package com.gcp.practise.parking.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleEntity implements java.io.Serializable {

    private static final long serialVersionUID = 4l;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "plate_normalized", nullable = false, unique = true)
    private String plateNormalized;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "user_id", insertable = false, updatable = false)
    private Integer userId;
}