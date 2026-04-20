package com.brunovirsan.orderflow.notificationservice.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka.topics")
public record KafkaTopicsProperties(
        String orderCreated,
        String orderConfirmed,
        String orderReady,
        String orderCancelled
) {
}
