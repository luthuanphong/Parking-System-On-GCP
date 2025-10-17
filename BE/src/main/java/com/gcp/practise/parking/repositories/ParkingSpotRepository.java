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
        value = CacheConfiguration.CACHE_NAME,
        key = CacheConfiguration.PARKING_SPOTS
    )
    List<ParkingSpotEntity> findAll();
}