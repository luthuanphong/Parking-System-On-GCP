package com.gcp.practise.parking.repositories;

import com.gcp.practise.parking.entities.ParkingSpotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpotEntity, Integer> {
    // TODO: Add custom query methods if needed
}