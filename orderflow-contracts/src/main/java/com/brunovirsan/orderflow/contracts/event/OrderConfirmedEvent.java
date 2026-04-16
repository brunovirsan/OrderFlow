package com.brunovirsan.orderflow.contracts.event;

import java.time.Instant;
import java.util.UUID;

public record OrderConfirmedEvent(
        UUID eventId,
        UUID orderId,
        String correlationId,
        Instant occurredAt,
        UUID kitchenTicketId
) implements DomainEvent {
}
