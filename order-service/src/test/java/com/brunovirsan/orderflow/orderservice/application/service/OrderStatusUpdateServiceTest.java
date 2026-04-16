package com.brunovirsan.orderflow.orderservice.application.service;

import com.brunovirsan.orderflow.contracts.event.OrderConfirmedEvent;
import com.brunovirsan.orderflow.orderservice.application.port.out.OrderRepository;
import com.brunovirsan.orderflow.orderservice.application.port.out.ProcessedEventRepository;
import com.brunovirsan.orderflow.orderservice.domain.model.Order;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderItem;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderStatus;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderStatusUpdateServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProcessedEventRepository processedEventRepository;

    @InjectMocks
    private OrderStatusUpdateService orderStatusUpdateService;

    @Test
    void shouldConfirmOrderFromKitchenEvent() {
        Order order = existingOrder(OrderStatus.PENDING);
        OrderConfirmedEvent event = new OrderConfirmedEvent(UUID.randomUUID(), order.getId(), "corr-123", Instant.now(), UUID.randomUUID());
        when(processedEventRepository.exists(event.eventId())).thenReturn(false);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderStatusUpdateService.handleOrderConfirmed(event, "order.confirmed");

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONFIRMED);
        verify(processedEventRepository).save(event.eventId(), "order.confirmed");
    }

    @Test
    void shouldIgnoreDuplicatedEvent() {
        OrderConfirmedEvent event = new OrderConfirmedEvent(UUID.randomUUID(), UUID.randomUUID(), "corr-123", Instant.now(), UUID.randomUUID());
        when(processedEventRepository.exists(event.eventId())).thenReturn(true);

        orderStatusUpdateService.handleOrderConfirmed(event, "order.confirmed");

        verify(orderRepository, never()).findById(any());
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
