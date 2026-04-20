package com.brunovirsan.orderflow.notificationservice.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "order_recipients")
public class OrderRecipientJpaEntity {

    @Id
    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    public String getRecipientEmail() { return recipientEmail; }
    public void setRecipientEmail(String recipientEmail) { this.recipientEmail = recipientEmail; }
}
