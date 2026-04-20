package com.brunovirsan.orderflow.notificationservice.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {

    private final UUID id;
    private final UUID orderId;
    private final String recipientEmail;
    private final NotificationType type;
    private final String message;
    private final LocalDateTime sentAt;
    private final NotificationStatus status;

    public Notification(UUID id,
                        UUID orderId,
                        String recipientEmail,
                        NotificationType type,
                        String message,
                        LocalDateTime sentAt,
                        NotificationStatus status) {
        this.id = id;
        this.orderId = orderId;
        this.recipientEmail = recipientEmail;
        this.type = type;
        this.message = message;
        this.sentAt = sentAt;
        this.status = status;
    }

    public static Notification sent(UUID orderId, String recipientEmail, NotificationType type, String message) {
        return new Notification(UUID.randomUUID(), orderId, recipientEmail, type, message, LocalDateTime.now(), NotificationStatus.SENT);
    }

    public UUID getId() { return id; }
    public UUID getOrderId() { return orderId; }
    public String getRecipientEmail() { return recipientEmail; }
    public NotificationType getType() { return type; }
    public String getMessage() { return message; }
    public LocalDateTime getSentAt() { return sentAt; }
    public NotificationStatus getStatus() { return status; }
}
