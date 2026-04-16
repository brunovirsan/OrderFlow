package com.brunovirsan.orderflow.kitchenservice.domain.model;

import com.brunovirsan.orderflow.kitchenservice.domain.exception.InvalidTicketStatusException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class KitchenTicketTest {

    @Test
    void shouldMoveTicketFromReceivedToReady() {
        KitchenTicket ticket = KitchenTicket.receive(
                UUID.randomUUID(),
                "Joao Silva",
                List.of(new TicketItem(UUID.randomUUID(), "X-Burger", 2))
        );

        ticket.updateStatus(TicketStatus.IN_PREPARATION);
        ticket.updateStatus(TicketStatus.READY);

        assertThat(ticket.getStatus()).isEqualTo(TicketStatus.READY);
    }

    @Test
    void shouldRejectJumpToReadyFromReceived() {
        KitchenTicket ticket = existingTicket(TicketStatus.RECEIVED);

        assertThatThrownBy(() -> ticket.updateStatus(TicketStatus.READY))
                .isInstanceOf(InvalidTicketStatusException.class)
                .hasMessageContaining("RECEIVED");
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
