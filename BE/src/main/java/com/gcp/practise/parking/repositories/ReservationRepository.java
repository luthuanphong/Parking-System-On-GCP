package com.gcp.practise.parking.repositories;

import com.gcp.practise.parking.entities.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    List<ReservationEntity> findByReservedForDate(LocalDate reservedForDate);
    boolean existsBySpotIdAndReservedForDate(Integer spotId, LocalDate reservedForDate);
}