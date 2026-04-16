package com.brunovirsan.orderflow.kitchenservice.api.dto;

import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record KitchenTicketResponse(
        UUID id,
        UUID orderId,
        String customerName,
        TicketStatus status,
        List<KitchenTicketItemResponse> items,
        LocalDateTime receivedAt,
        LocalDateTime updatedAt
) {
}
