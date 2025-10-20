package com.gcp.practise.parking.configurations;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
@ConfigurationProperties(prefix = "application.features.version")
public class FeaturesConfiguration {
    private Integer booking;
}
