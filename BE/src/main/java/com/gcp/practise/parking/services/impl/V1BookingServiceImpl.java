package com.gcp.practise.parking.services.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.gcp.practise.parking.concurent.BookingProcessingQueue;
import com.gcp.practise.parking.dtos.BookingProcessing;
import com.gcp.practise.parking.repositories.ParkingSpotRepository;
import com.gcp.practise.parking.services.ParkingLotService;

@Service(V1BookingServiceImpl.INSTANCE_ID)
@ConditionalOnProperty(name = "application.features.version.booking", havingValue = "1")
public class V1BookingServiceImpl extends BaseBookingServiceImpl {

    public static final String INSTANCE_ID = "V1BookingServiceImpl";

    private final BookingProcessingQueue bookingProcessingQueue;

    public V1BookingServiceImpl(
        ParkingSpotRepository parkingSpotRepository, 
        ParkingLotService parkingLotService,
        CacheManager cacheManager, BookingProcessingQueue bookingProcessingQueue) {
        super(parkingSpotRepository, parkingLotService, cacheManager);
        this.bookingProcessingQueue = bookingProcessingQueue;
    }

    @Override
    protected void proceedRequest(BookingProcessing processing) {
        bookingProcessingQueue.addToQueue(processing);
    }

}
