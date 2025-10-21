package com.gcp.practise.parking.concurent.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.gcp.practise.parking.common.CacheConfiguration;
import com.gcp.practise.parking.entities.VehicleEntity;
import com.gcp.practise.parking.repositories.VehicleRepository;
import com.gcp.practise.parking.security.CustomUserDetails;



@Component
@ConditionalOnProperty(name = "application.features.version.booking", havingValue = "1")

public class RepositoryCacheProcessor implements DisposableBean {

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    private final VehicleRepository vehicleRepository;

    private final CacheManager cacheManager;

    public RepositoryCacheProcessor(
        VehicleRepository vehicleRepository,
        CacheManager cacheManager
    ) {
        this.cacheManager = cacheManager;
        this.vehicleRepository = vehicleRepository;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void syncAll() {
        Cache userDetail = cacheManager.getCache(CacheConfiguration.USER_CACHE_NAME);
        Cache userRepoCache = cacheManager.getCache(CacheConfiguration.USER_REPOSITORY_CACHE);
        Cache vehicleRepoCahce = cacheManager.getCache(CacheConfiguration.VEHICLE_REPOSITORY_CACHE);
        Cache allVehiclesCache = cacheManager.getCache(CacheConfiguration.ALL_VEHICLES_CACHE);
        executor.submit(() -> {
            userDetail.clear();
            userRepoCache.clear();
            vehicleRepoCahce.clear();
            allVehiclesCache.clear();

            try (Stream<VehicleEntity> vStream = vehicleRepository.getAllVehicle()) {
                java.util.List<VehicleEntity> allVehicles = new java.util.ArrayList<>();
                vStream.forEach(v -> {
                    vehicleRepoCahce.put(v.getUserId(), v);
                    userRepoCache.put(v.getUser().getEmail(), v.getUser());
                    userDetail.put(v.getUser(), new CustomUserDetails(v.getUser(), v));
                    allVehicles.add(v);
                }); 
                // Cache all vehicles under a single key
                allVehiclesCache.put("ALL_VEHICLES", allVehicles);
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        executor.shutdown();
    }
}
