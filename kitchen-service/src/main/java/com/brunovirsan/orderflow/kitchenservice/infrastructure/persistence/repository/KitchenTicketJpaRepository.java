package com.brunovirsan.orderflow.kitchenservice.infrastructure.persistence.repository;

import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketStatus;
import com.brunovirsan.orderflow.kitchenservice.infrastructure.persistence.entity.KitchenTicketJpaEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KitchenTicketJpaRepository extends JpaRepository<KitchenTicketJpaEntity, UUID> {

    @Override
    @EntityGraph(attributePaths = "items")
    Optional<KitchenTicketJpaEntity> findById(UUID id);

    @EntityGraph(attributePaths = "items")
    Optional<KitchenTicketJpaEntity> findByOrderId(UUID orderId);

    @EntityGraph(attributePaths = "items")
    List<KitchenTicketJpaEntity> findAllByOrderByReceivedAtDesc();

    @EntityGraph(attributePaths = "items")
    List<KitchenTicketJpaEntity> findByStatusOrderByReceivedAtDesc(TicketStatus status);
}
