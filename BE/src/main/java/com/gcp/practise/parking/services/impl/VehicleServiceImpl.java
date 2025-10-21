package com.gcp.practise.parking.services.impl;

import com.gcp.practise.parking.common.CacheConfiguration;
import com.gcp.practise.parking.dtos.responses.VehicleResponse;
import com.gcp.practise.parking.entities.VehicleEntity;
import com.gcp.practise.parking.repositories.VehicleRepository;
import com.gcp.practise.parking.services.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CacheManager cacheManager;

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles() {
        Cache allVehiclesCache = cacheManager.getCache(CacheConfiguration.ALL_VEHICLES_CACHE);
        
        if (allVehiclesCache != null) {
            @SuppressWarnings("unchecked")
            List<VehicleEntity> cachedVehicles = allVehiclesCache.get("ALL_VEHICLES", List.class);
            
            if (cachedVehicles != null) {
                return cachedVehicles.stream()
                        .map(this::mapToVehicleResponse)
                        .collect(Collectors.toList());
            }
        }
        
        // Fallback to repository if cache is not available or empty
        return vehicleRepository.getAllVehicle()
                .map(this::mapToVehicleResponse)
                .collect(Collectors.toList());
    }

    private VehicleResponse mapToVehicleResponse(VehicleEntity entity) {
        return VehicleResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .plateNormalized(entity.getPlateNormalized())
                .createdAt(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)
                .build();
    }
}