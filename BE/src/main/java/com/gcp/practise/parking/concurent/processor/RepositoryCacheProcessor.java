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
import com.gcp.practise.parking.entities.UserEntity;
import com.gcp.practise.parking.entities.VehicleEntity;
import com.gcp.practise.parking.repositories.UserRepository;
import com.gcp.practise.parking.repositories.VehicleRepository;
import com.gcp.practise.parking.security.CustomUserDetails;


@Component
@ConditionalOnProperty(name = "application.features.version.booking", havingValue = "1")
public class RepositoryCacheProcessor implements DisposableBean {

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private CacheManager cacheManager;

    @EventListener(ApplicationStartedEvent.class)
    public void syncAll() {
        Cache userDetail = cacheManager.getCache(CacheConfiguration.USER_CACHE_NAME);
        Cache userRepoCache = cacheManager.getCache(CacheConfiguration.USER_REPOSITORY_CACHE);
        Cache vehicleRepoCahce = cacheManager.getCache(CacheConfiguration.VEHICLE_REPOSITORY_CACHE);
        executor.submit(() -> {
            userDetail.clear();
            userRepoCache.clear();
            vehicleRepoCahce.clear();

            try (Stream<VehicleEntity> vStream = vehicleRepository.getAllVehicle()) {
                vStream.forEach(v -> {
                    vehicleRepoCahce.put(v.getUserId(), v);
                    userRepoCache.put(v.getUser().getEmail(), v.getUser());
                    userDetail.put(v.getUser(), new CustomUserDetails(v.getUser(), v));
                }); 
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        executor.shutdown();
    }
}
