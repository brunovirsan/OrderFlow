package com.brunovirsan.orderflow.kitchenservice.application.service;

import com.brunovirsan.orderflow.contracts.event.OrderCreatedEvent;
import com.brunovirsan.orderflow.contracts.model.OrderItemPayload;
import com.brunovirsan.orderflow.kitchenservice.application.port.out.KitchenEventPublisher;
import com.brunovirsan.orderflow.kitchenservice.application.port.out.KitchenTicketRepository;
import com.brunovirsan.orderflow.kitchenservice.application.port.out.ProcessedEventRepository;
import com.brunovirsan.orderflow.kitchenservice.domain.model.KitchenTicket;
import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketItem;
import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KitchenTicketApplicationServiceTest {

    @Mock
    private KitchenTicketRepository kitchenTicketRepository;

    @Mock
    private KitchenEventPublisher kitchenEventPublisher;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private KitchenTicketApplicationService kitchenTicketApplicationService;

    @Test
    void shouldReceiveOrderAndPublishConfirmation() {
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
        when(kitchenTicketRepository.save(any(KitchenTicket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        kitchenTicketApplicationService.receiveOrder(event, "order.created");

        verify(kitchenTicketRepository).save(any(KitchenTicket.class));
        verify(kitchenEventPublisher).publishConfirmed(any(KitchenTicket.class), anyString());
        verify(processedEventRepository).save(event.eventId(), "order.created");
    }

    @Test
    void shouldSkipDuplicatedIncomingEvent() {
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
        when(processedEventRepository.exists(event.eventId())).thenReturn(true);

        kitchenTicketApplicationService.receiveOrder(event, "order.created");

        verify(kitchenTicketRepository, never()).save(any());
        verify(kitchenEventPublisher, never()).publishConfirmed(any(), anyString());
    }

    @Test
    void shouldPublishStatusChangedWhenMovingToPreparation() {
        KitchenTicket ticket = existingTicket(TicketStatus.RECEIVED);
        when(kitchenTicketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        when(kitchenTicketRepository.save(any(KitchenTicket.class))).thenAnswer(invocation -> invocation.getArgument(0));

        KitchenTicket updated = kitchenTicketApplicationService.updateStatus(ticket.getId(), TicketStatus.IN_PREPARATION);

        verify(kitchenEventPublisher).publishStatusChanged(any(KitchenTicket.class), any(TicketStatus.class), anyString());
        assertThat(updated.getStatus()).isEqualTo(TicketStatus.IN_PREPARATION);
    }

    private KitchenTicket existingTicket(TicketStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return new KitchenTicket(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Joao Silva",
                List.of(new TicketItem(UUID.randomUUID(), "X-Burger", 1)),
                status,
                now,
                now
        );
    }
}
