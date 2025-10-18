package com.gcp.practise.parking.concurent.processor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.gcp.practise.parking.concurent.BookingProcessingQueue;
import com.gcp.practise.parking.dtos.BookingProcessing;
import com.gcp.practise.parking.entities.ReservationEntity;
import com.gcp.practise.parking.services.ParkingLotService;
import com.gcp.practise.parking.utils.DateUtils;

@Component
public class BookingRequestProcessor implements DisposableBean {
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    private volatile boolean running = true;

    @Autowired
    private ParkingLotService parkingLotService;

    @Autowired
    private BookingProcessingQueue queue;

    @EventListener(ApplicationStartedEvent.class)
    public void star() {
        executor.submit(() -> {
            while (running) {
                if (!queue.isEmpty()) {
                    
                    BookingProcessing processing = queue.getNextInQueue();
                    var currentReservations = parkingLotService.getCurrentReservations();
                    Integer spotId = processing.getRequest().getSpotId();
                    var request = processing.getRequest();
                    var userDetails = processing.getUserDetails();
                    boolean isAlreadyReserved = currentReservations.stream()
                            .anyMatch(reservation -> reservation.getSpotId().equals(spotId));

                    if (isAlreadyReserved) {
                        continue;
                    }

                    LocalDate targetDate = DateUtils.getTargetDate();
                    // Create new reservation
                    ReservationEntity reservation = ReservationEntity.builder()
                        .spotId(request.getSpotId())
                        .userId(userDetails.getUserId())
                        .vehicleId(userDetails.getVehicleId())
                        .reservedForDate(targetDate)
                        .createdAt(OffsetDateTime.now())
                        .build();
                        
                    ReservationEntity savedReservation = reservationRepository.save(reservation);

                    userDetails.setBalanceCents(userDetails.getBalanceCents() - 1000);
                    userDetailsServiceImpl.updateUserBalance(userDetails);
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
