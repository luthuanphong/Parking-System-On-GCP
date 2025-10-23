package com.gcp.practise.parking.jobs;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.gcp.practise.parking.common.CacheConfiguration;
import com.gcp.practise.parking.entities.ReservationEntity;
import com.gcp.practise.parking.repositories.ReservationRepository;
import com.gcp.practise.parking.utils.DateUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "application.features.version.booking", havingValue = "2")
@Slf4j
public class CacheRefreshScheduler {

    private final CacheManager cacheManager;

    private final ReservationRepository reservationRepository;

    public CacheRefreshScheduler(
        CacheManager cacheManager,
        ReservationRepository reservationRepository
        ) {
        this.cacheManager = cacheManager;
        this.reservationRepository = reservationRepository;
    }

    /**
     * Scheduled task that runs daily at 8 PM
     * Cron expression: "0 0 20 * * ?" 
     * - Second: 0
     * - Minute: 0 
     * - Hour: 20 (8 PM in 24-hour format)
     * - Day of month: * (any)
     * - Month: * (any)
     * - Day of week: ? (any)
     */
    // @Scheduled(cron = "0 0 20 * * ?")
    // public void dailyCacheRefreshAt8PM() {
    //     log.info("Cache refresh scheduler triggered at 8 PM");
        
    //     Cache reservated = cacheManager.getCache(CacheConfiguration.RESERVATIONS_CACHE_NAME);
    //     Cache reservedUserCache = cacheManager.getCache(CacheConfiguration.RESERVED_USER_CACHE_NAME);

    //     if (reservated != null) reservated.clear();
    //     if (reservedUserCache != null) reservedUserCache.clear(); 
        
    //     log.info("Cache refresh scheduler completed");
    // }

    @Scheduled(cron = "0 0,15,30,45 * * * ?")
    // @Scheduled(fixedDelay = 15, timeUnit = TimeUnit.MINUTES)
    public void refreshCache() {
        LocalDate localDate = DateUtils.getTargetDate();
        Cache reservated = cacheManager.getCache(CacheConfiguration.RESERVATIONS_CACHE_NAME);
        Cache reservedUserCache = cacheManager.getCache(CacheConfiguration.RESERVED_USER_CACHE_NAME);
        Cache userRequestCache = cacheManager.getCache(CacheConfiguration.USER_RESERVATION_REQUEST_CACHE_NAME);
        List<ReservationEntity> entities = reservationRepository.findByReservedForDate(localDate);
        if (CollectionUtils.isEmpty(entities)) {
            reservated.clear();
            reservedUserCache.clear(); 
            userRequestCache.clear();
        }
    }
}
