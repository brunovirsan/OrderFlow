package com.brunovirsan.orderflow.notificationservice.infrastructure.messaging.consumer;

import com.brunovirsan.orderflow.contracts.event.OrderCreatedEvent;
import com.brunovirsan.orderflow.notificationservice.application.service.NotificationApplicationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private final NotificationApplicationService notificationApplicationService;

    public OrderCreatedConsumer(NotificationApplicationService notificationApplicationService) {
        this.notificationApplicationService = notificationApplicationService;
    }

    @KafkaListener(topics = "${app.kafka.topics.order-created}", groupId = "notification-service")
    public void onOrderCreated(OrderCreatedEvent event, @Header(name = "kafka_receivedTopic") String topic, Acknowledgment acknowledgment) {
        notificationApplicationService.registerRecipient(event, topic);
        acknowledgment.acknowledge();
    }
}
