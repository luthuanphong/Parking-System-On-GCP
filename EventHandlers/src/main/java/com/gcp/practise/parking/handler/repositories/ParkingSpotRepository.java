package com.gcp.practise.parking.handler.repositories;

import com.gcp.practise.parking.handler.common.CacheConfiguration;
import com.gcp.practise.parking.handler.entities.ParkingSpotEntity;

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