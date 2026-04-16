package com.brunovirsan.orderflow.contracts.event;

import java.time.Instant;
import java.util.UUID;

public record OrderInPreparationEvent(
        UUID eventId,
        UUID orderId,
        String correlationId,
        Instant occurredAt
) implements DomainEvent {
}
