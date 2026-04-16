package com.brunovirsan.orderflow.contracts.event;

import java.time.Instant;
import java.util.UUID;

public record OrderCancelledEvent(
        UUID eventId,
        UUID orderId,
        String correlationId,
        Instant occurredAt,
        String reason
) implements DomainEvent {
}
