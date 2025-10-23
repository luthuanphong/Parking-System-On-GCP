package com.gcp.practise.parking.repositories;

import com.gcp.practise.parking.common.CacheConfiguration;
import com.gcp.practise.parking.entities.ParkingSpotEntity;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSpotRepository extends JpaRepository<ParkingSpotEntity, Integer> {

    @Cacheable(
        value = CacheConfiguration.PARKING_SPOTS_CACHE_NAME
    )
    List<ParkingSpotEntity> findAll();
}