package com.brunovirsan.orderflow.orderservice.infrastructure.messaging.consumer;

import com.brunovirsan.orderflow.contracts.event.OrderInPreparationEvent;
import com.brunovirsan.orderflow.orderservice.application.service.OrderStatusUpdateService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderInPreparationConsumer {

    private final OrderStatusUpdateService orderStatusUpdateService;

    public OrderInPreparationConsumer(OrderStatusUpdateService orderStatusUpdateService) {
        this.orderStatusUpdateService = orderStatusUpdateService;
    }

    @KafkaListener(topics = "${app.kafka.topics.order-in-preparation}", groupId = "order-service")
    public void onOrderInPreparation(OrderInPreparationEvent event,
                                     @Header(name = "kafka_receivedTopic") String topic,
                                     Acknowledgment acknowledgment) {
        orderStatusUpdateService.handleOrderInPreparation(event, topic);
        acknowledgment.acknowledge();
    }
}
