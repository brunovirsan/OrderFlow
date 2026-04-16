package com.brunovirsan.orderflow.orderservice.infrastructure.persistence.repository;

import com.brunovirsan.orderflow.orderservice.domain.model.OrderStatus;
import com.brunovirsan.orderflow.orderservice.infrastructure.persistence.entity.OrderJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = "items")
    Optional<OrderJpaEntity> findById(UUID id);

    @EntityGraph(attributePaths = "items")
    List<OrderJpaEntity> findByStatusOrderByCreatedAtDesc(OrderStatus status);

    @EntityGraph(attributePaths = "items")
    List<OrderJpaEntity> findAllByOrderByCreatedAtDesc();
}
