package com.brunovirsan.orderflow.kitchenservice.infrastructure.messaging.producer;

import com.brunovirsan.orderflow.contracts.event.OrderConfirmedEvent;
import com.brunovirsan.orderflow.contracts.event.OrderInPreparationEvent;
import com.brunovirsan.orderflow.contracts.event.OrderReadyEvent;
import com.brunovirsan.orderflow.kitchenservice.application.port.out.KitchenEventPublisher;
import com.brunovirsan.orderflow.kitchenservice.domain.model.KitchenTicket;
import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketStatus;
import com.brunovirsan.orderflow.kitchenservice.infrastructure.config.KafkaTopicsProperties;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@Component
public class KitchenKafkaProducer implements KitchenEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final KafkaTopicsProperties kafkaTopicsProperties;

    public KitchenKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate,
                                KafkaTopicsProperties kafkaTopicsProperties) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaTopicsProperties = kafkaTopicsProperties;
    }

    @Override
    public void publishConfirmed(KitchenTicket ticket, String correlationId) {
        OrderConfirmedEvent event = new OrderConfirmedEvent(
                UUID.randomUUID(),
                ticket.getOrderId(),
                correlationId,
                ticket.getReceivedAt().toInstant(ZoneOffset.UTC),
                ticket.getId()
        );
        kafkaTemplate.send(kafkaTopicsProperties.orderConfirmed(), ticket.getOrderId().toString(), event);
    }

    @Override
    public void publishStatusChanged(KitchenTicket ticket, TicketStatus status, String correlationId) {
        Object event = switch (status) {
            case IN_PREPARATION -> new OrderInPreparationEvent(
                    UUID.randomUUID(),
                    ticket.getOrderId(),
                    correlationId,
                    Instant.now()
            );
            case READY -> new OrderReadyEvent(
                    UUID.randomUUID(),
                    ticket.getOrderId(),
                    correlationId,
                    Instant.now()
            );
            default -> throw new IllegalArgumentException("Unsupported status %s".formatted(status));
        };

        String topic = status == TicketStatus.IN_PREPARATION
                ? kafkaTopicsProperties.orderInPreparation()
                : kafkaTopicsProperties.orderReady();
        kafkaTemplate.send(topic, ticket.getOrderId().toString(), event);
    }
}
