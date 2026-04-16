package com.brunovirsan.orderflow.orderservice.domain.model;

import com.brunovirsan.orderflow.orderservice.domain.exception.InvalidOrderStatusException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OrderTest {

    @Test
    void shouldCalculateTotalWhenCreatingOrder() {
        Order order = Order.create(
                "Joao Silva",
                "joao@email.com",
                List.of(
                        new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "X-Burger", 2, BigDecimal.valueOf(25.90)),
                        new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "Refrigerante", 1, BigDecimal.valueOf(7.50))
                )
        );

        assertThat(order.getTotalAmount()).isEqualByComparingTo("59.30");
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PENDING);
    }

    @Test
    void shouldRejectInvalidTransitionWhenStartingPreparationWithoutConfirmation() {
        Order order = existingOrder(OrderStatus.PENDING);

        assertThatThrownBy(order::startPreparation)
                .isInstanceOf(InvalidOrderStatusException.class)
                .hasMessageContaining("CONFIRMED");
    }

    @Test
    void shouldCancelOrderWhilePending() {
        Order order = existingOrder(OrderStatus.PENDING);

        order.cancel("Cliente desistiu");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        assertThat(order.getCancellationReason()).isEqualTo("Cliente desistiu");
    }

    private Order existingOrder(OrderStatus status) {
        LocalDateTime now = LocalDateTime.now();
        return new Order(
                UUID.randomUUID(),
                "Joao Silva",
                "joao@email.com",
                List.of(new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "X-Burger", 1, BigDecimal.TEN)),
                status,
                BigDecimal.TEN,
                now,
                now,
                null
        );
    }
}
