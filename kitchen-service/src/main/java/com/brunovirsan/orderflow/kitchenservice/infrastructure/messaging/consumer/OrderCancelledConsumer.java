package com.brunovirsan.orderflow.kitchenservice.infrastructure.messaging.consumer;

import com.brunovirsan.orderflow.contracts.event.OrderCancelledEvent;
import com.brunovirsan.orderflow.kitchenservice.application.service.KitchenTicketApplicationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderCancelledConsumer {

    private final KitchenTicketApplicationService kitchenTicketApplicationService;

    public OrderCancelledConsumer(KitchenTicketApplicationService kitchenTicketApplicationService) {
        this.kitchenTicketApplicationService = kitchenTicketApplicationService;
    }

    @KafkaListener(topics = "${app.kafka.topics.order-cancelled}", groupId = "kitchen-service")
    public void onOrderCancelled(OrderCancelledEvent event,
                                 @Header(name = "kafka_receivedTopic") String topic,
                                 Acknowledgment acknowledgment) {
        kitchenTicketApplicationService.cancelOrder(event, topic);
        acknowledgment.acknowledge();
    }
}
