package com.brunovirsan.orderflow.notificationservice.application.service;

import com.brunovirsan.orderflow.contracts.event.OrderCancelledEvent;
import com.brunovirsan.orderflow.contracts.event.OrderConfirmedEvent;
import com.brunovirsan.orderflow.contracts.event.OrderCreatedEvent;
import com.brunovirsan.orderflow.contracts.event.OrderReadyEvent;
import com.brunovirsan.orderflow.notificationservice.application.port.out.NotificationRepository;
import com.brunovirsan.orderflow.notificationservice.application.port.out.OrderRecipientRepository;
import com.brunovirsan.orderflow.notificationservice.application.port.out.ProcessedEventRepository;
import com.brunovirsan.orderflow.notificationservice.domain.model.Notification;
import com.brunovirsan.orderflow.notificationservice.domain.model.NotificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class NotificationApplicationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationApplicationService.class);

    private final NotificationRepository notificationRepository;
    private final OrderRecipientRepository orderRecipientRepository;
    private final ProcessedEventRepository processedEventRepository;

    public NotificationApplicationService(NotificationRepository notificationRepository,
                                          OrderRecipientRepository orderRecipientRepository,
                                          ProcessedEventRepository processedEventRepository) {
        this.notificationRepository = notificationRepository;
        this.orderRecipientRepository = orderRecipientRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public void registerRecipient(OrderCreatedEvent event, String topic) {
        if (processedEventRepository.exists(event.eventId())) {
            return;
        }
        orderRecipientRepository.save(event.orderId(), event.customerEmail());
        processedEventRepository.save(event.eventId(), topic);
    }

    @Transactional
    public void notifyOrderConfirmed(OrderConfirmedEvent event, String topic) {
        createNotificationIfPossible(event.eventId(), event.orderId(), topic, NotificationType.ORDER_CONFIRMED,
                buildMessage(NotificationType.ORDER_CONFIRMED, null));
    }

    @Transactional
    public void notifyOrderReady(OrderReadyEvent event, String topic) {
        createNotificationIfPossible(event.eventId(), event.orderId(), topic, NotificationType.ORDER_READY,
                buildMessage(NotificationType.ORDER_READY, null));
    }

    @Transactional
    public void notifyOrderCancelled(OrderCancelledEvent event, String topic) {
        createNotificationIfPossible(event.eventId(), event.orderId(), topic, NotificationType.ORDER_CANCELLED,
                buildMessage(NotificationType.ORDER_CANCELLED, event.reason()));
    }

    public String buildMessage(NotificationType type, String cancellationReason) {
        return switch (type) {
            case ORDER_CONFIRMED -> "Seu pedido foi confirmado e enviado para a cozinha.";
            case ORDER_READY -> "Seu pedido esta pronto para retirada.";
            case ORDER_CANCELLED -> "Seu pedido foi cancelado. Motivo: %s".formatted(cancellationReason);
        };
    }

    private void createNotificationIfPossible(UUID eventId,
                                              UUID orderId,
                                              String topic,
                                              NotificationType type,
                                              String message) {
        if (processedEventRepository.exists(eventId)) {
            return;
        }

        orderRecipientRepository.findRecipientEmail(orderId).ifPresent(email -> {
            Notification notification = notificationRepository.save(Notification.sent(orderId, email, type, message));
            LOGGER.info("notification sent orderId={} recipientEmail={} type={}",
                    notification.getOrderId(), notification.getRecipientEmail(), notification.getType());
        });
        processedEventRepository.save(eventId, topic);
    }
}
