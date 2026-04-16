package com.brunovirsan.orderflow.orderservice.domain.model;

import com.brunovirsan.orderflow.orderservice.domain.exception.InvalidOrderStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final String customerName;
    private final String customerEmail;
    private final List<OrderItem> items;
    private OrderStatus status;
    private final BigDecimal totalAmount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String cancellationReason;

    public Order(UUID id,
                 String customerName,
                 String customerEmail,
                 List<OrderItem> items,
                 OrderStatus status,
                 BigDecimal totalAmount,
                 LocalDateTime createdAt,
                 LocalDateTime updatedAt,
                 String cancellationReason) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.items = List.copyOf(items);
        this.status = status;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.cancellationReason = cancellationReason;
    }

    public static Order create(String customerName, String customerEmail, List<OrderItem> items) {
        LocalDateTime now = LocalDateTime.now();
        return new Order(
                UUID.randomUUID(),
                customerName,
                customerEmail,
                items,
                OrderStatus.PENDING,
                items.stream().map(OrderItem::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add),
                now,
                now,
                null
        );
    }

    public void cancel(String reason) {
        ensureStatusNot(OrderStatus.READY, OrderStatus.DELIVERED, OrderStatus.CANCELLED);
        this.status = OrderStatus.CANCELLED;
        this.cancellationReason = reason;
        touch();
    }

    public void confirm() {
        ensureStatus(OrderStatus.PENDING);
        this.status = OrderStatus.CONFIRMED;
        touch();
    }

    public void startPreparation() {
        ensureStatus(OrderStatus.CONFIRMED);
        this.status = OrderStatus.IN_PREPARATION;
        touch();
    }

    public void markReady() {
        ensureStatus(OrderStatus.IN_PREPARATION);
        this.status = OrderStatus.READY;
        touch();
    }

    private void ensureStatus(OrderStatus expected) {
        if (this.status != expected) {
            throw new InvalidOrderStatusException("Order status must be %s but was %s".formatted(expected, status));
        }
    }

    private void ensureStatusNot(OrderStatus... forbiddenStatuses) {
        for (OrderStatus forbiddenStatus : forbiddenStatuses) {
            if (this.status == forbiddenStatus) {
                throw new InvalidOrderStatusException("Order status %s does not allow this operation".formatted(status));
            }
        }
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }
}
