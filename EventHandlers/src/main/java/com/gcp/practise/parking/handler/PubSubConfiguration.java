package com.gcp.practise.parking.handler;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcp.practise.parking.handler.dtos.BookingRequestPubsubMessage;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;

@Configuration
public class PubSubConfiguration {

    private static final String SUBSCRIPTION_KEY = "parking-request-consume";

    @Bean
    public JacksonPubSubMessageConverter jacksonPubSubMessageConverter(ObjectMapper objectMapper) {
        return new JacksonPubSubMessageConverter(objectMapper);
    }

    @Bean("pubSubInputChannel")
    public DirectChannel pubSubInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(
        @Qualifier("pubSubInputChannel") MessageChannel inputChannel,
        PubSubTemplate pubSubTemplate,
        @Qualifier("subscriptionName") String subscriptionName) {
        PubSubInboundChannelAdapter adapter =
            new PubSubInboundChannelAdapter(pubSubTemplate, SUBSCRIPTION_KEY);
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.MANUAL);
        adapter.setPayloadType(BookingRequestPubsubMessage.class);
        return adapter;
    }
}
