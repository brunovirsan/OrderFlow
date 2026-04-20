package com.brunovirsan.orderflow.notificationservice.infrastructure.messaging.consumer;

import com.brunovirsan.orderflow.contracts.event.OrderConfirmedEvent;
import com.brunovirsan.orderflow.notificationservice.application.service.NotificationApplicationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmedConsumer {

    private final NotificationApplicationService notificationApplicationService;

    public OrderConfirmedConsumer(NotificationApplicationService notificationApplicationService) {
        this.notificationApplicationService = notificationApplicationService;
    }

    @KafkaListener(topics = "${app.kafka.topics.order-confirmed}", groupId = "notification-service")
    public void onOrderConfirmed(OrderConfirmedEvent event, @Header(name = "kafka_receivedTopic") String topic, Acknowledgment acknowledgment) {
        notificationApplicationService.notifyOrderConfirmed(event, topic);
        acknowledgment.acknowledge();
    }
}
