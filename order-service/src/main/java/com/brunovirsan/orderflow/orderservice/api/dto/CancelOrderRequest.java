package com.brunovirsan.orderflow.orderservice.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelOrderRequest(
        @NotBlank(message = "Cancellation reason is required")
        String reason
) {
}
