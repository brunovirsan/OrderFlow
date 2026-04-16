package com.brunovirsan.orderflow.kitchenservice.domain.model;

import com.brunovirsan.orderflow.kitchenservice.domain.exception.InvalidTicketStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class KitchenTicket {

    private final UUID id;
    private final UUID orderId;
    private final String customerName;
    private final List<TicketItem> items;
    private TicketStatus status;
    private final LocalDateTime receivedAt;
    private LocalDateTime updatedAt;

    public KitchenTicket(UUID id,
                         UUID orderId,
                         String customerName,
                         List<TicketItem> items,
                         TicketStatus status,
                         LocalDateTime receivedAt,
                         LocalDateTime updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.customerName = customerName;
        this.items = List.copyOf(items);
        this.status = status;
        this.receivedAt = receivedAt;
        this.updatedAt = updatedAt;
    }

    public static KitchenTicket receive(UUID orderId, String customerName, List<TicketItem> items) {
        LocalDateTime now = LocalDateTime.now();
        return new KitchenTicket(UUID.randomUUID(), orderId, customerName, items, TicketStatus.RECEIVED, now, now);
    }

    public void updateStatus(TicketStatus targetStatus) {
        if (targetStatus == TicketStatus.IN_PREPARATION && this.status == TicketStatus.RECEIVED) {
            this.status = targetStatus;
            touch();
            return;
        }
        if (targetStatus == TicketStatus.READY && this.status == TicketStatus.IN_PREPARATION) {
            this.status = targetStatus;
            touch();
            return;
        }
        if (targetStatus == TicketStatus.CANCELLED && this.status != TicketStatus.READY) {
            this.status = targetStatus;
            touch();
            return;
        }
        throw new InvalidTicketStatusException("Ticket status %s cannot transition to %s".formatted(this.status, targetStatus));
    }

    private void touch() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<TicketItem> getItems() {
        return items;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public LocalDateTime getReceivedAt() {
        return receivedAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
