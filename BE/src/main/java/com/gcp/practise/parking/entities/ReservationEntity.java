package com.gcp.practise.parking.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "reservations", indexes = {
        @Index(columnList = "spot_id, reserved_for_date"),
        @Index(columnList = "vehicle_id, reserved_for_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", insertable = false, updatable = false)
    private VehicleEntity vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "spot_id", insertable = false, updatable = false)
    private ParkingSpotEntity spot;

    @Column(name = "reserved_for_date", nullable = false)
    private LocalDate reservedForDate;

    @Column(name = "amount_cents", nullable = false)
    @Builder.Default
    private Long amountCents = 1000L;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "vehicle_id")
    private Integer vehicleId;

    @Column(name = "spot_id")
    private Integer spotId;

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return super.hashCode();
    }
}
