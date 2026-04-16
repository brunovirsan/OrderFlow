package com.brunovirsan.orderflow.orderservice.infrastructure.persistence.adapter;

import com.brunovirsan.orderflow.orderservice.application.port.out.OrderRepository;
import com.brunovirsan.orderflow.orderservice.domain.model.Order;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderItem;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderStatus;
import com.brunovirsan.orderflow.orderservice.infrastructure.persistence.entity.OrderItemJpaEntity;
import com.brunovirsan.orderflow.orderservice.infrastructure.persistence.entity.OrderJpaEntity;
import com.brunovirsan.orderflow.orderservice.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OrderPersistenceAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    public OrderPersistenceAdapter(OrderJpaRepository orderJpaRepository) {
        this.orderJpaRepository = orderJpaRepository;
    }

    @Override
    public Order save(Order order) {
        return toDomain(orderJpaRepository.save(toEntity(order)));
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        return orderJpaRepository.findById(orderId).map(this::toDomain);
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orderJpaRepository.findByStatusOrderByCreatedAtDesc(status).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Order> findAll() {
        return orderJpaRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toDomain).toList();
    }

    private OrderJpaEntity toEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(order.getId());
        entity.setCustomerName(order.getCustomerName());
        entity.setCustomerEmail(order.getCustomerEmail());
        entity.setStatus(order.getStatus());
        entity.setTotalAmount(order.getTotalAmount());
        entity.setCreatedAt(order.getCreatedAt());
        entity.setUpdatedAt(order.getUpdatedAt());
        entity.setCancellationReason(order.getCancellationReason());
        entity.setItems(order.getItems().stream().map(item -> toEntity(item, entity)).toList());
        return entity;
    }

    private OrderItemJpaEntity toEntity(OrderItem item, OrderJpaEntity order) {
        OrderItemJpaEntity entity = new OrderItemJpaEntity();
        entity.setId(item.getId());
        entity.setOrder(order);
        entity.setProductId(item.getProductId());
        entity.setProductName(item.getProductName());
        entity.setQuantity(item.getQuantity());
        entity.setUnitPrice(item.getUnitPrice());
        return entity;
    }

    private Order toDomain(OrderJpaEntity entity) {
        return new Order(
                entity.getId(),
                entity.getCustomerName(),
                entity.getCustomerEmail(),
                entity.getItems().stream().map(this::toDomain).toList(),
                entity.getStatus(),
                entity.getTotalAmount(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getCancellationReason()
        );
    }

    private OrderItem toDomain(OrderItemJpaEntity entity) {
        return new OrderItem(
                entity.getId(),
                entity.getProductId(),
                entity.getProductName(),
                entity.getQuantity(),
                entity.getUnitPrice()
        );
    }
}
