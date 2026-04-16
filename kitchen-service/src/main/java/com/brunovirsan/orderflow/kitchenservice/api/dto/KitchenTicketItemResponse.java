package com.brunovirsan.orderflow.kitchenservice.api.dto;

import java.util.UUID;

public record KitchenTicketItemResponse(
        UUID id,
        String productName,
        Integer quantity
) {
}
