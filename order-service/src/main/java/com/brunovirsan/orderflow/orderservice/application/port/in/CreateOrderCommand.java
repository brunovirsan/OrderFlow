package com.brunovirsan.orderflow.orderservice.application.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateOrderCommand(
        String customerName,
        String customerEmail,
        List<CreateOrderItemCommand> items
) {

    public record CreateOrderItemCommand(
            UUID productId,
            String productName,
            Integer quantity,
            BigDecimal unitPrice
    ) {
    }
}
