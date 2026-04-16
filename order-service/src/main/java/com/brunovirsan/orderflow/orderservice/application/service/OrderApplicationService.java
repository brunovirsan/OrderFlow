package com.brunovirsan.orderflow.orderservice.application.service;

import com.brunovirsan.orderflow.contracts.observability.CorrelationContext;
import com.brunovirsan.orderflow.orderservice.application.port.in.CancelOrderUseCase;
import com.brunovirsan.orderflow.orderservice.application.port.in.CreateOrderCommand;
import com.brunovirsan.orderflow.orderservice.application.port.in.CreateOrderUseCase;
import com.brunovirsan.orderflow.orderservice.application.port.in.GetOrderUseCase;
import com.brunovirsan.orderflow.orderservice.application.port.out.OrderEventPublisher;
import com.brunovirsan.orderflow.orderservice.application.port.out.OrderRepository;
import com.brunovirsan.orderflow.orderservice.domain.exception.OrderNotFoundException;
import com.brunovirsan.orderflow.orderservice.domain.model.Order;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderItem;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderStatus;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class OrderApplicationService implements CreateOrderUseCase, GetOrderUseCase, CancelOrderUseCase {

    private final OrderRepository orderRepository;
    private final OrderEventPublisher orderEventPublisher;

    public OrderApplicationService(OrderRepository orderRepository,
                                   OrderEventPublisher orderEventPublisher) {
        this.orderRepository = orderRepository;
        this.orderEventPublisher = orderEventPublisher;
    }

    @Override
    @Transactional
    public Order create(CreateOrderCommand command) {
        Order newOrder = Order.create(
                command.customerName(),
                command.customerEmail(),
                command.items().stream().map(this::toOrderItem).toList()
        );

        Order savedOrder = orderRepository.save(newOrder);
        orderEventPublisher.publishOrderCreated(savedOrder, currentCorrelationId());
        return savedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Order findById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Order> findByStatus(OrderStatus status) {
        if (status == null) {
            return orderRepository.findAll();
        }
        return orderRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public void cancel(UUID orderId, String reason) {
        Order order = findById(orderId);
        order.cancel(reason);
        Order savedOrder = orderRepository.save(order);
        orderEventPublisher.publishOrderCancelled(savedOrder, currentCorrelationId(), reason);
    }

    private OrderItem toOrderItem(CreateOrderCommand.CreateOrderItemCommand item) {
        return new OrderItem(UUID.randomUUID(), item.productId(), item.productName(), item.quantity(), item.unitPrice());
    }

    private String currentCorrelationId() {
        String correlationId = MDC.get(CorrelationContext.MDC_KEY);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = CorrelationContext.generateCorrelationId();
            MDC.put(CorrelationContext.MDC_KEY, correlationId);
        }
        return correlationId;
    }
}
