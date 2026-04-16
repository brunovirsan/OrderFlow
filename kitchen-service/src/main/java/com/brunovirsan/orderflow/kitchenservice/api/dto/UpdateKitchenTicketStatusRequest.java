package com.brunovirsan.orderflow.kitchenservice.api.dto;

import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateKitchenTicketStatusRequest(
        @NotNull(message = "Status is required")
        TicketStatus status
) {
}
