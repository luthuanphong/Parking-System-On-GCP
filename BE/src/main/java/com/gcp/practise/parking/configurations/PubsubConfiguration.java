package com.gcp.practise.parking.configurations;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageHandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gcp.practise.parking.dtos.BookingRequestPubsubMessage;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.outbound.PubSubMessageHandler;
import com.google.cloud.spring.pubsub.support.converter.JacksonPubSubMessageConverter;

import lombok.extern.slf4j.Slf4j;

@Configuration
@ConditionalOnProperty(name = "application.features.version.booking", havingValue = "2")
@Slf4j
public class PubsubConfiguration {
    private static final String TOPIC_NAME = "parking-request-topic";

    @Bean
    public JacksonPubSubMessageConverter jacksonPubSubMessageConverter(ObjectMapper objectMapper) {
        return new JacksonPubSubMessageConverter(objectMapper);
    }

    @Bean
    public DirectChannel pubSubOutputChannel() {
        return new DirectChannel();
    }

    @Bean
    @ServiceActivator(inputChannel = "pubSubOutputChannel")
    public MessageHandler messageSender(
        PubSubTemplate pubSubTemplate) {
        PubSubMessageHandler adapter = new PubSubMessageHandler(pubSubTemplate, TOPIC_NAME);
        adapter.setSuccessCallback((ackId, message) -> log.info("Message was sent successfully."));
        adapter.setFailureCallback((cause, message) -> log.warn("There was an error sending the message."));
        return adapter;
    }

    /** an interface that allows sending a person to Pub/Sub. */
    @MessagingGateway(defaultRequestChannel = "pubSubOutputChannel")
    public interface PubSubGateway {
        void publish(BookingRequestPubsubMessage person);
    }
}
