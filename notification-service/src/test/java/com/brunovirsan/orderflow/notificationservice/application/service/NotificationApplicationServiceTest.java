package com.brunovirsan.orderflow.notificationservice.application.service;

import com.brunovirsan.orderflow.contracts.event.OrderCreatedEvent;
import com.brunovirsan.orderflow.contracts.event.OrderReadyEvent;
import com.brunovirsan.orderflow.contracts.model.OrderItemPayload;
import com.brunovirsan.orderflow.notificationservice.application.port.out.NotificationRepository;
import com.brunovirsan.orderflow.notificationservice.application.port.out.OrderRecipientRepository;
import com.brunovirsan.orderflow.notificationservice.application.port.out.ProcessedEventRepository;
import com.brunovirsan.orderflow.notificationservice.domain.model.Notification;
import com.brunovirsan.orderflow.notificationservice.domain.model.NotificationType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationApplicationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private OrderRecipientRepository orderRecipientRepository;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private NotificationApplicationService notificationApplicationService;

    @Test
    void shouldBuildCancellationMessage() {
        String message = notificationApplicationService.buildMessage(NotificationType.ORDER_CANCELLED, "Cliente desistiu");
        assertThat(message).contains("Cliente desistiu");
    }

    @Test
    void shouldPersistReadyNotificationWhenRecipientExists() {
        UUID orderId = UUID.randomUUID();
        when(processedEventRepository.exists(any())).thenReturn(false);
        when(orderRecipientRepository.findRecipientEmail(orderId)).thenReturn(Optional.of("joao@email.com"));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(invocation -> invocation.getArgument(0));

        notificationApplicationService.notifyOrderReady(new OrderReadyEvent(UUID.randomUUID(), orderId, "corr-123", Instant.now()), "order.ready");

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getType()).isEqualTo(NotificationType.ORDER_READY);
    }

    @Test
    void shouldRegisterRecipientFromOrderCreated() {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "corr-123",
                Instant.now(),
                "Joao Silva",
                "joao@email.com",
                BigDecimal.valueOf(51.80),
                List.of(new OrderItemPayload(UUID.randomUUID(), "X-Burger", 2, BigDecimal.valueOf(25.90)))
        );
        when(processedEventRepository.exists(event.eventId())).thenReturn(false);

        notificationApplicationService.registerRecipient(event, "order.created");

        verify(orderRecipientRepository).save(event.orderId(), "joao@email.com");
    }
}
