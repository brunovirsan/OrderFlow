package com.brunovirsan.orderflow.contracts.model;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemPayload(
        UUID productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice
) {
}
