package com.gcp.practise.parking;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.gcp.practise.parking.common.CacheConfiguration;
import com.gcp.practise.parking.dtos.BookingRequestPubsubMessage;
import com.gcp.practise.parking.entities.ReservationEntity;
import com.gcp.practise.parking.entities.UserEntity;
import com.gcp.practise.parking.entities.VehicleEntity;
import com.gcp.practise.parking.repositories.ReservationRepository;
import com.gcp.practise.parking.repositories.UserRepository;
import com.gcp.practise.parking.repositories.VehicleRepository;
import com.gcp.practise.parking.security.CustomUserDetails;
import com.gcp.practise.parking.utils.DateUtils;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookingRequestHandler {

    private final Semaphore semaphore = new Semaphore(1);

    private final CacheManager cacheManager;

    private final ReservationRepository reservationRepository;

    private final VehicleRepository vehicleRepository;

    private final UserRepository userRepository;

    @ServiceActivator(inputChannel = "pubSubInputChannel")
    public void handleIncomingRequest(
        BookingRequestPubsubMessage payload,
         @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message
    ) {
        try {
            log.info("Message arrived: {}", payload);
            LocalDate targetDate = DateUtils.getTargetDate();
            ReservationEntity reservation = ReservationEntity.builder()
                            .spotId(payload.getSpotId())
                            .userId(payload.getUserId())
                            .vehicleId(payload.getVehicleId())
                            .reservedForDate(targetDate)
                            .createdAt(OffsetDateTime.now())
                            .build();
            semaphore.acquire();
            Cache reservated = cacheManager.getCache(CacheConfiguration.RESERVATIONS_CACHE_NAME);
            Cache reservedUserCache = cacheManager.getCache(CacheConfiguration.RESERVED_USER_CACHE_NAME);
            if (reservedUserCache.putIfAbsent(payload.getUserId(), payload.getUsername()) == null) {
                if (reservated.putIfAbsent(payload.getSpotId(), reservation) == null) {
                    Cache reservationsOfTheDay = cacheManager.getCache(CacheConfiguration.CACHE_NAME);
                    List<ReservationEntity> reservations = reservationsOfTheDay.get(targetDate.toString(), List.class);
                    if (reservations == null) {
                        reservations = new ArrayList<>();
                    }
                    reservation = reservationRepository.save(reservation);
                    reservations.add(reservation);
                    reservationsOfTheDay.put(targetDate.toString(), reservations);
                    reduceBalance(reservation.getVehicleId());
                } else {
                    log.info("spot is marked as reservated: {}", payload.getSpotId());
                    // Spot already reserved, evict user reservation
                    reservedUserCache.evict(payload.getUserId());
                }
            } else {
                log.info("User is marked as reservated: {}", payload.getUserId());
            }
            
            cacheManager.getCache(CacheConfiguration.USER_RESERVATION_REQUEST_CACHE_NAME)
                .evict(payload.getUsername());

            semaphore.release();

            message.ack();
        } catch (Exception e) {
            log.error("Failed to accquire semaphore", e);
        }
    } 

    private void reduceBalance(Integer vehicleId) {
        VehicleEntity vehicle = vehicleRepository.findByIdOrThrow(vehicleId);
        UserEntity user = vehicle.getUser();
        user.setBalanceCents(user.getBalanceCents() - 1000);

        user = userRepository.save(user);
        Cache userDetail = cacheManager.getCache(CacheConfiguration.USER_CACHE_NAME);
        Cache userRepoCache = cacheManager.getCache(CacheConfiguration.USER_REPOSITORY_CACHE);
        userRepoCache.put(user.getEmail(), user);
        userDetail.put(user.getEmail(), new CustomUserDetails(user, vehicle));
    }
}
