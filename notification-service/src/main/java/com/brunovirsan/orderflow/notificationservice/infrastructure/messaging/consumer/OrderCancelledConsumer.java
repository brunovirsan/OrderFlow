package com.brunovirsan.orderflow.notificationservice.infrastructure.messaging.consumer;

import com.brunovirsan.orderflow.contracts.event.OrderCancelledEvent;
import com.brunovirsan.orderflow.notificationservice.application.service.NotificationApplicationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderCancelledConsumer {

    private final NotificationApplicationService notificationApplicationService;

    public OrderCancelledConsumer(NotificationApplicationService notificationApplicationService) {
        this.notificationApplicationService = notificationApplicationService;
    }

    @KafkaListener(topics = "${app.kafka.topics.order-cancelled}", groupId = "notification-service")
    public void onOrderCancelled(OrderCancelledEvent event, @Header(name = "kafka_receivedTopic") String topic, Acknowledgment acknowledgment) {
        notificationApplicationService.notifyOrderCancelled(event, topic);
        acknowledgment.acknowledge();
    }
}
