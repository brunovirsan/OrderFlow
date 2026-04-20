package com.brunovirsan.orderflow.notificationservice.infrastructure.messaging.consumer;

import com.brunovirsan.orderflow.contracts.event.OrderReadyEvent;
import com.brunovirsan.orderflow.notificationservice.application.service.NotificationApplicationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderReadyConsumer {

    private final NotificationApplicationService notificationApplicationService;

    public OrderReadyConsumer(NotificationApplicationService notificationApplicationService) {
        this.notificationApplicationService = notificationApplicationService;
    }

    @KafkaListener(topics = "${app.kafka.topics.order-ready}", groupId = "notification-service")
    public void onOrderReady(OrderReadyEvent event, @Header(name = "kafka_receivedTopic") String topic, Acknowledgment acknowledgment) {
        notificationApplicationService.notifyOrderReady(event, topic);
        acknowledgment.acknowledge();
    }
}
