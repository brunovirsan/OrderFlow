package com.brunovirsan.orderflow.kitchenservice.infrastructure.messaging.consumer;

import com.brunovirsan.orderflow.contracts.event.OrderCreatedEvent;
import com.brunovirsan.orderflow.kitchenservice.application.service.KitchenTicketApplicationService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@Component
public class OrderCreatedConsumer {

    private final KitchenTicketApplicationService kitchenTicketApplicationService;

    public OrderCreatedConsumer(KitchenTicketApplicationService kitchenTicketApplicationService) {
        this.kitchenTicketApplicationService = kitchenTicketApplicationService;
    }

    @KafkaListener(topics = "${app.kafka.topics.order-created}", groupId = "kitchen-service")
    public void onOrderCreated(OrderCreatedEvent event,
                               @Header(name = "kafka_receivedTopic") String topic,
                               Acknowledgment acknowledgment) {
        kitchenTicketApplicationService.receiveOrder(event, topic);
        acknowledgment.acknowledge();
    }
}
