package com.brunovirsan.orderflow.orderservice.infrastructure.messaging.consumer;

import com.brunovirsan.orderflow.contracts.event.OrderConfirmedEvent;
import com.brunovirsan.orderflow.orderservice.application.service.OrderStatusUpdateService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderConfirmedConsumer {

    private final OrderStatusUpdateService orderStatusUpdateService;

    public OrderConfirmedConsumer(OrderStatusUpdateService orderStatusUpdateService) {
        this.orderStatusUpdateService = orderStatusUpdateService;
    }

    @KafkaListener(topics = "${app.kafka.topics.order-confirmed}", groupId = "order-service")
    public void onOrderConfirmed(OrderConfirmedEvent event,
                                 @Header(name = "kafka_receivedTopic") String topic,
                                 Acknowledgment acknowledgment) {
        orderStatusUpdateService.handleOrderConfirmed(event, topic);
        acknowledgment.acknowledge();
    }
}
