package com.gcp.practise.parking.concurent.processor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.gcp.practise.parking.common.CacheConfiguration;
import com.gcp.practise.parking.concurent.BookingProcessingQueue;
import com.gcp.practise.parking.dtos.BookingProcessing;
import com.gcp.practise.parking.entities.ReservationEntity;
import com.gcp.practise.parking.entities.UserEntity;
import com.gcp.practise.parking.repositories.ReservationRepository;
import com.gcp.practise.parking.repositories.UserRepository;
import com.gcp.practise.parking.security.CustomUserDetails;
import com.gcp.practise.parking.utils.DateUtils;

@Component
public class BookingRequestProcessor implements DisposableBean {

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ExecutorService reservationInserter = Executors.newSingleThreadExecutor();

    private volatile boolean running = true;

    @Autowired
    private BookingProcessingQueue queue;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserRepository userRepository;

    @EventListener(ApplicationStartedEvent.class)
    public void star() {
        executor.submit(() -> {
            while (running) {
                if (!queue.isEmpty()) {

                    BookingProcessing processing = queue.getNextInQueue();
                    var request = processing.getRequest();
                    var userDetails = processing.getUserDetails();
                    LocalDate targetDate = DateUtils.getTargetDate();
                    // Create new reservation
                    ReservationEntity reservation = ReservationEntity.builder()
                        .spotId(request.getSpotId())
                        .userId(userDetails.getUserId())
                        .vehicleId(userDetails.getVehicleId())
                        .vehicle(userDetails.getVehicle())
                        .user(userDetails.getUser())
                        .reservedForDate(targetDate)
                        .createdAt(OffsetDateTime.now())
                        .build();

                    Cache reservated = cacheManager.getCache(CacheConfiguration.RESERVATIONS_CACHE_NAME);
                    Cache reservedUserCache = cacheManager.getCache(CacheConfiguration.RESERVED_USER_CACHE_NAME);
                    
                    if (reservedUserCache.putIfAbsent(userDetails.getUserId(), userDetails.getUsername()) == null) {
                        if (reservated.putIfAbsent(request.getSpotId(), reservation) == null) {
                            Cache reservationsOfTheDay = cacheManager.getCache(CacheConfiguration.CACHE_NAME);
                            List<ReservationEntity> reservations = reservationsOfTheDay.get(targetDate.toString(), List.class);
                            if (reservations == null) {
                                reservations = new ArrayList<>();
                            }

                            reservations.add(reservation);
                            reservationsOfTheDay.put(targetDate.toString(), reservations);
                        } else {
                            // Spot already reserved, evict user reservation
                            reservedUserCache.evict(userDetails.getUserId());
                        }
                    }

                    cacheManager.getCache(CacheConfiguration.USER_RESERVATION_REQUEST_CACHE_NAME)
                        .evict(userDetails.getUsername());
                } else {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @EventListener(ApplicationStartedEvent.class)
    public void processReservations() {
        reservationInserter.submit(() -> {
            while (running) {
                try {
                    Thread.sleep(2000);
                    LocalDate targetDate = DateUtils.getTargetDate();
                    Cache reservationsOfTheDay = cacheManager.getCache(CacheConfiguration.CACHE_NAME);
                    List<ReservationEntity> reservations = reservationsOfTheDay.get(targetDate.toString(), List.class);
                    if (!CollectionUtils.isEmpty(reservations)) {
                        List<ReservationEntity> insertedReservations = reservations
                        .stream().filter(reservation -> reservation.getId() != null)
                        .toList();

                        List<ReservationEntity> toBeInserted = reservations
                        .stream().filter(reservation -> reservation.getId() == null)
                        .toList();

                        toBeInserted = reservationRepository.saveAll(toBeInserted);
                        updateUsers(toBeInserted);

                        List<ReservationEntity> finalToBeInserted = new ArrayList<>(toBeInserted);
                        finalToBeInserted.addAll(insertedReservations);
                        reservationsOfTheDay.put(targetDate.toString(), finalToBeInserted);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    
    @Override
    public void destroy() throws Exception {
        running = false;
        executor.shutdown();
        reservationInserter.shutdown();
    }

    private void updateUsers(List<ReservationEntity> reservations) {
        List<UserEntity> usersToUpdate = reservations.stream().map(
            reservation -> reservation.getUser())
            .distinct()
            .map(u -> {
                u.setBalanceCents(u.getBalanceCents() - 1000); // Deduct 1000 cents ($10) for booking
                return u;
            }).toList();

        userRepository.saveAll(usersToUpdate);
        Cache userCache = cacheManager.getCache(CacheConfiguration.USER_CACHE_NAME);
        reservations.stream().forEach(reservation -> {
            CustomUserDetails userDetails = reservation.getUserDetails();
            userDetails.setBalanceCents(reservation.getUser().getBalanceCents() - 1000);
            if (userDetails != null) {
                userDetails.setBalanceCents(userDetails.getBalanceCents() - 1000);
                userCache.put(userDetails.getUsername(), userDetails);
            }
        });
    }
}
