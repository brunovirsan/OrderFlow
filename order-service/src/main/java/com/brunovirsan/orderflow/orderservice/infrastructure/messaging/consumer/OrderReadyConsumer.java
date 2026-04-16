package com.brunovirsan.orderflow.orderservice.infrastructure.messaging.consumer;

import com.brunovirsan.orderflow.contracts.event.OrderReadyEvent;
import com.brunovirsan.orderflow.orderservice.application.service.OrderStatusUpdateService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderReadyConsumer {

    private final OrderStatusUpdateService orderStatusUpdateService;

    public OrderReadyConsumer(OrderStatusUpdateService orderStatusUpdateService) {
        this.orderStatusUpdateService = orderStatusUpdateService;
    }

    @KafkaListener(topics = "${app.kafka.topics.order-ready}", groupId = "order-service")
    public void onOrderReady(OrderReadyEvent event,
                             @Header(name = "kafka_receivedTopic") String topic,
                             Acknowledgment acknowledgment) {
        orderStatusUpdateService.handleOrderReady(event, topic);
        acknowledgment.acknowledge();
    }
}
