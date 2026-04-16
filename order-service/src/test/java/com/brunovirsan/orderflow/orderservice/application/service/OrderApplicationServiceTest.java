package com.brunovirsan.orderflow.orderservice.application.service;

import com.brunovirsan.orderflow.orderservice.application.port.in.CreateOrderCommand;
import com.brunovirsan.orderflow.orderservice.application.port.out.OrderEventPublisher;
import com.brunovirsan.orderflow.orderservice.application.port.out.OrderRepository;
import com.brunovirsan.orderflow.orderservice.domain.exception.OrderNotFoundException;
import com.brunovirsan.orderflow.orderservice.domain.model.Order;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderItem;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderApplicationServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderEventPublisher orderEventPublisher;

    @InjectMocks
    private OrderApplicationService orderApplicationService;

    @Test
    void shouldCreateOrderAndPublishCreatedEvent() {
        CreateOrderCommand command = new CreateOrderCommand(
                "Joao Silva",
                "joao@email.com",
                List.of(new CreateOrderCommand.CreateOrderItemCommand(
                        UUID.randomUUID(),
                        "X-Burger",
                        2,
                        BigDecimal.valueOf(25.90)))
        );
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Order createdOrder = orderApplicationService.create(command);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        verify(orderEventPublisher).publishOrderCreated(any(Order.class), anyString());

        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(orderCaptor.getValue().getTotalAmount()).isEqualByComparingTo("51.80");
    }

    @Test
    void shouldCancelOrderAndPublishCancelledEvent() {
        Order order = existingOrder(OrderStatus.PENDING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        orderApplicationService.cancel(order.getId(), "Cliente desistiu");

        verify(orderEventPublisher).publishOrderCancelled(any(Order.class), anyString(), anyString());
        assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    @Test
    void shouldThrowWhenOrderDoesNotExist() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderApplicationService.findById(orderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining(orderId.toString());
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
