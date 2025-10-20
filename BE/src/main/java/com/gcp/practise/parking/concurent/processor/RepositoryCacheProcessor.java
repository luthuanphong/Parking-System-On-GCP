package com.gcp.practise.parking.concurent.processor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
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


@Component
public class RepositoryCacheProcessor implements DisposableBean {

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    private volatile boolean running = true;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private CacheManager cacheManager;

    @EventListener(ApplicationStartedEvent.class)
    public void syncAll() {
        executor.submit(this::syncUser);
        executor.submit(this::syncVehicle);
    }

    @Override
    public void destroy() throws Exception {
        executor.shutdown();
    }

    private void syncUser() {
        Cache cache = cacheManager.getCache(CacheConfiguration.USER_REPOSITORY_CACHE);
        try(Stream<UserEntity> uStream = userRepository.streamAll()) {
            uStream.forEach(user -> cache.put(user.getEmail(), user));
        }
    }

    private void syncVehicle() {
        Cache cache = cacheManager.getCache(CacheConfiguration.VEHICLE_REPOSITORY_CACHE);
        try(Stream<VehicleEntity> vStream = vehicleRepository.streamAll()) {
            vStream.forEach(vehicle -> cache.put(vehicle.getUserId(), vehicle));
        }
    }
}
