package com.gcp.practise.parking.handler.repositories;

import com.gcp.practise.parking.handler.common.CacheConfiguration;
import com.gcp.practise.parking.handler.entities.VehicleEntity;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Integer> {
    Optional<VehicleEntity> findByUserId(Integer userId);

    @Cacheable(
        value = CacheConfiguration.VEHICLE_REPOSITORY_BY_USER_ID_CACHE,
        key = "#userId"
    )
    default VehicleEntity findByUserIdOrThrow(Integer userId) {
        return findByUserId(userId).orElseThrow();
    }

    @Cacheable(
        value = CacheConfiguration.VEHICLE_REPOSITORY_BY_ID_CACHE,
        key = "#id"
    )
    default VehicleEntity findByIdOrThrow(Integer id) {
        return findById(id).orElseThrow();
    }

    @Cacheable(
        value = CacheConfiguration.ALL_VEHICLE,
        key = CacheConfiguration.ALL_VEHICLE
    )
    List<VehicleEntity> findAll();

    @Query("""
            SELECT v FROM VehicleEntity v
            """)
    Stream<VehicleEntity> getAllVehicle();
}