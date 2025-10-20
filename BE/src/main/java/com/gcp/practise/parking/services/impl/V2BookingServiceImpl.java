package com.gcp.practise.parking.services.impl;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import com.gcp.practise.parking.dtos.BookingProcessing;
import com.gcp.practise.parking.dtos.BookingRequestPubsubMessage;
import com.gcp.practise.parking.repositories.ParkingSpotRepository;
import com.gcp.practise.parking.services.ParkingLotService;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;

@Service(V2BookingServiceImpl.INSTANCE_ID)
@ConditionalOnProperty(name = "application.features.version.booking", havingValue = "2")
public class V2BookingServiceImpl extends BaseBookingServiceImpl {

    public static final String INSTANCE_ID = "V2BookingServiceImpl";

    private final PubSubTemplate pubSubTemplate;

    private final String TOPIC = "parking-request-topic";

    public V2BookingServiceImpl(
        ParkingSpotRepository parkingSpotRepository, 
        ParkingLotService parkingLotService,
        CacheManager cacheManager,
        PubSubTemplate pubSubTemplate) {
        super(parkingSpotRepository, parkingLotService, cacheManager);
        this.pubSubTemplate = pubSubTemplate;
    }

    @Override
    protected void proceedRequest(BookingProcessing processing) {
        var request = processing.getRequest();
        var userDetail = processing.getUserDetails();
        pubSubTemplate.publish(TOPIC, 
        BookingRequestPubsubMessage.builder()
        .spotId(request.getSpotId())
        .userId(userDetail.getUserId())
        .username(userDetail.getUsername())
        .vehicleId(userDetail.getVehicleId())
        .build());
    }

}
