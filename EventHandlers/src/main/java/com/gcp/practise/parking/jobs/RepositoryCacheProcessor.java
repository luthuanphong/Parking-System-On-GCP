package com.gcp.practise.parking.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.gcp.practise.parking.common.CacheConfiguration;
import com.gcp.practise.parking.entities.VehicleEntity;
import com.gcp.practise.parking.repositories.VehicleRepository;
import com.gcp.practise.parking.security.CustomUserDetails;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConditionalOnProperty(name = "application.features.version.booking", havingValue = "2")
public class RepositoryCacheProcessor implements DisposableBean {

    private final ExecutorService executor = Executors.newFixedThreadPool(1);

    private final VehicleRepository vehicleRepository;

    private final CacheManager cacheManager;

    private final PlatformTransactionManager transactionManager;

    private volatile boolean running = true;

    public RepositoryCacheProcessor(
        VehicleRepository vehicleRepository,
        CacheManager cacheManager,
        PlatformTransactionManager transactionManager
    ) {
        this.cacheManager = cacheManager;
        this.vehicleRepository = vehicleRepository;
        this.transactionManager = transactionManager;
    }

    @EventListener(ApplicationStartedEvent.class)
    public void syncAll() {
        Cache userDetail = cacheManager.getCache(CacheConfiguration.USER_CACHE_NAME);
        Cache userRepoCache = cacheManager.getCache(CacheConfiguration.USER_REPOSITORY_CACHE);
        Cache vehicleRepoCahce = cacheManager.getCache(CacheConfiguration.VEHICLE_REPOSITORY_BY_USER_ID_CACHE);
        Cache vehicleRepoByIDCahce = cacheManager.getCache(CacheConfiguration.VEHICLE_REPOSITORY_BY_ID_CACHE);
        Cache allVehicles = cacheManager.getCache(CacheConfiguration.ALL_VEHICLE);
        executor.execute(() -> {
            userDetail.clear();
            userRepoCache.clear();
            vehicleRepoCahce.clear();
            vehicleRepoByIDCahce.clear();
            while (running) {
                DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
                definition.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRED);
                definition.setReadOnly(true);
                TransactionStatus status = transactionManager.getTransaction(definition);
                try (Stream<VehicleEntity> vStream = vehicleRepository.getAllVehicle()) {
                    List<VehicleEntity> entities = new ArrayList<>();
                    vStream.forEach(v -> {
                        log.info("Input Vehicle into cache: {}", v);
                        vehicleRepoCahce.put(v.getUserId(), v);
                        vehicleRepoByIDCahce.put(v.getId(), v);
                        userRepoCache.put(v.getUser().getEmail(), v.getUser());
                        userDetail.put(v.getUser().getEmail(), new CustomUserDetails(v.getUser(), v));
                        entities.add(v);
                    }); 

                    allVehicles.put(CacheConfiguration.ALL_VEHICLE, entities);
                    transactionManager.commit(status);
                }

                try {
                    Thread.sleep(1800000);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void destroy() throws Exception {
        running = false;
        executor.shutdown();
    }
}
