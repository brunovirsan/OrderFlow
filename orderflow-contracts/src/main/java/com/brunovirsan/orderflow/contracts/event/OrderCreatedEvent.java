package com.brunovirsan.orderflow.contracts.event;

import com.brunovirsan.orderflow.contracts.model.OrderItemPayload;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID eventId,
        UUID orderId,
        String correlationId,
        Instant occurredAt,
        String customerName,
        String customerEmail,
        BigDecimal totalAmount,
        List<OrderItemPayload> items
) implements DomainEvent {
}
