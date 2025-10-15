package com.gcp.practise.parking.repositories;

import com.gcp.practise.parking.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    // TODO: Add custom query methods if needed
}