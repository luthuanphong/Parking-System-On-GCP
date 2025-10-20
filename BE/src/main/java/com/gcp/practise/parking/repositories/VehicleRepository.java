package com.gcp.practise.parking.repositories;

import com.gcp.practise.parking.common.CacheConfiguration;
import com.gcp.practise.parking.entities.VehicleEntity;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleEntity, Integer> {
    Optional<VehicleEntity> findByUserId(Integer userId);

    @Cacheable(
        value = CacheConfiguration.VEHICLE_REPOSITORY_CACHE,
        key = "#userId"
    )
    default VehicleEntity findByUserIdOrThrow(Integer userId) {
        return findByUserId(userId).orElseThrow();
    }

    Stream<VehicleEntity> streamAll();
}