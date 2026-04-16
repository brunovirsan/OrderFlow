package com.brunovirsan.orderflow.kitchenservice.domain.model;

import java.util.UUID;

public class TicketItem {

    private final UUID id;
    private final String productName;
    private final Integer quantity;

    public TicketItem(UUID id, String productName, Integer quantity) {
        this.id = id;
        this.productName = productName;
        this.quantity = quantity;
    }

    public UUID getId() {
        return id;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
