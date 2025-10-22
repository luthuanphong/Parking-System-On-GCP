package com.gcp.practise.parking.handler.jobs;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.gcp.practise.parking.handler.common.CacheConfiguration;

import lombok.extern.slf4j.Slf4j;

@Component
@ConditionalOnProperty(name = "application.features.version.booking", havingValue = "2")
@Slf4j
public class CacheRefreshScheduler {

    private final CacheManager cacheManager;

    public CacheRefreshScheduler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
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
    @Scheduled(cron = "0 0 20 * * ?")
    public void dailyCacheRefreshAt8PM() {
        log.info("Cache refresh scheduler triggered at 8 PM");
        
        Cache reservated = cacheManager.getCache(CacheConfiguration.RESERVATIONS_CACHE_NAME);
        Cache reservedUserCache = cacheManager.getCache(CacheConfiguration.RESERVED_USER_CACHE_NAME);

        if (reservated != null) reservated.clear();
        if (reservedUserCache != null) reservedUserCache.clear(); 
        
        log.info("Cache refresh scheduler completed");
    }
}
