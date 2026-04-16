package com.brunovirsan.orderflow.kitchenservice.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record KafkaTopicsProperties(
        String orderCreated,
        String orderCancelled,
        String orderConfirmed,
        String orderInPreparation,
        String orderReady
) {
}
