package com.gcp.practise.parking.handler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.gcp.practise.parking.handler.common.CacheConfiguration;
import com.gcp.practise.parking.handler.entities.ReservationEntity;
import com.gcp.practise.parking.handler.entities.UserEntity;
import com.gcp.practise.parking.handler.entities.VehicleEntity;
import com.gcp.practise.parking.handler.repositories.ReservationRepository;
import com.gcp.practise.parking.handler.repositories.UserRepository;
import com.gcp.practise.parking.handler.repositories.VehicleRepository;
import com.gcp.practise.parking.handler.security.CustomUserDetails;
import com.gcp.practise.parking.handler.utils.DateUtils;

@Component
@ConditionalOnProperty(name = "application.features.version.booking", havingValue = "2")
public class BookingRequestInserter implements DisposableBean {

    private final ExecutorService reservationInserter = Executors.newSingleThreadExecutor();

    private volatile boolean running = true;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;


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
        reservationInserter.shutdown();
    }

    private void updateUsers(List<ReservationEntity> reservations) {
        List<UserEntity> usersToUpdate = reservations
        .stream()
        .map(r -> r.getVehicleId())
        .map(vehicleRepository::findByIdOrThrow)
        .map(VehicleEntity::getUser)
        .map(u -> {
            u.setBalanceCents(u.getBalanceCents() - 1000);
            return u;
        }).toList();
        
        usersToUpdate = userRepository.saveAll(usersToUpdate);

        Cache userCache = cacheManager.getCache(CacheConfiguration.USER_CACHE_NAME);
        reservations
        .stream()
        .map(r -> r.getVehicleId())
        .map(vehicleRepository::findByIdOrThrow)
        .forEach(v -> {
            CustomUserDetails userDetails = new CustomUserDetails(v.getUser(), v);
            userDetails.setBalanceCents(v.getUser().getBalanceCents() - 1000);
            userCache.put(userDetails.getUsername(), userDetails);
        });
    }
}
