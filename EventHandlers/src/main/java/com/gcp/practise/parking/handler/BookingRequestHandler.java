package com.gcp.practise.parking.handler;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.gcp.practise.parking.handler.dtos.BookingRequestPubsubMessage;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;

@Component
public class BookingRequestHandler {

    @ServiceActivator(inputChannel = "pubSubInputChannel")
    public void handleIncomingRequest(
        BookingRequestPubsubMessage payload,
         @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message
    ) {
        
    } 

}
