package com.gcp.practise.parking.handler;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.gcp.practise.parking.handler.common.CacheConfiguration;
import com.gcp.practise.parking.handler.dtos.BookingRequestPubsubMessage;
import com.gcp.practise.parking.handler.entities.ReservationEntity;
import com.gcp.practise.parking.handler.repositories.ReservationRepository;
import com.gcp.practise.parking.handler.repositories.UserRepository;
import com.gcp.practise.parking.handler.repositories.VehicleRepository;
import com.gcp.practise.parking.handler.utils.DateUtils;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingRequestHandler {

    private final CacheManager cacheManager;

    @ServiceActivator(inputChannel = "pubSubInputChannel")
    public void handleIncomingRequest(
        BookingRequestPubsubMessage payload,
         @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message
    ) {
        log.info("Message arrived: {}", payload);
        LocalDate targetDate = DateUtils.getTargetDate();
        ReservationEntity reservation = ReservationEntity.builder()
                        .spotId(payload.getSpotId())
                        .userId(payload.getUserId())
                        .vehicleId(payload.getVehicleId())
                        .reservedForDate(targetDate)
                        .createdAt(OffsetDateTime.now())
                        .build();

        Cache reservated = cacheManager.getCache(CacheConfiguration.RESERVATIONS_CACHE_NAME);
        Cache reservedUserCache = cacheManager.getCache(CacheConfiguration.RESERVED_USER_CACHE_NAME);
        if (reservedUserCache.putIfAbsent(payload.getUserId(), payload.getUsername()) == null) {
            if (reservated.putIfAbsent(payload.getSpotId(), reservation) == null) {
                Cache reservationsOfTheDay = cacheManager.getCache(CacheConfiguration.CACHE_NAME);
                List<ReservationEntity> reservations = reservationsOfTheDay.get(targetDate.toString(), List.class);
                if (reservations == null) {
                    reservations = new ArrayList<>();
                }

                reservations.add(reservation);
                reservationsOfTheDay.put(targetDate.toString(), reservations);
            } else {
                // Spot already reserved, evict user reservation
                reservedUserCache.evict(payload.getUserId());
            }
        }

        cacheManager.getCache(CacheConfiguration.USER_RESERVATION_REQUEST_CACHE_NAME)
            .evict(payload.getUsername());

        message.ack();
    } 

}
