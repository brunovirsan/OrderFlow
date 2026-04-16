package com.brunovirsan.orderflow.orderservice.infrastructure.messaging.producer;

import com.brunovirsan.orderflow.contracts.event.OrderCancelledEvent;
import com.brunovirsan.orderflow.contracts.event.OrderCreatedEvent;
import com.brunovirsan.orderflow.contracts.model.OrderItemPayload;
import com.brunovirsan.orderflow.orderservice.application.port.out.OrderEventPublisher;
import com.brunovirsan.orderflow.orderservice.domain.model.Order;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderItem;
import com.brunovirsan.orderflow.orderservice.infrastructure.config.KafkaTopicsProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class OrderKafkaProducer implements OrderEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    public OrderKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate,
                              KafkaTopicsProperties kafkaTopicsProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicsProperties = kafkaTopicsProperties;
    }

    @Override
    public void publishOrderCreated(Order order, String correlationId) {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                order.getId(),
                correlationId,
                order.getCreatedAt().toInstant(ZoneOffset.UTC),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getTotalAmount(),
                order.getItems().stream().map(this::toPayload).toList()
        );
        kafkaTemplate.send(kafkaTopicsProperties.orderCreated(), order.getId().toString(), event);
    }

    @Override
    public void publishOrderCancelled(Order order, String correlationId, String reason) {
        OrderCancelledEvent event = new OrderCancelledEvent(
                UUID.randomUUID(),
                order.getId(),
                correlationId,
                Instant.now(),
                reason
        );
        kafkaTemplate.send(kafkaTopicsProperties.orderCancelled(), order.getId().toString(), event);
    }

    private OrderItemPayload toPayload(OrderItem item) {
        return new OrderItemPayload(item.getProductId(), item.getProductName(), item.getQuantity(), item.getUnitPrice());
    }
}
