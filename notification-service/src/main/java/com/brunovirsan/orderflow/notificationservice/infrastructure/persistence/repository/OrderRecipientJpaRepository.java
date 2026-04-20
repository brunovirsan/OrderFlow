package com.brunovirsan.orderflow.notificationservice.infrastructure.persistence.repository;

import com.brunovirsan.orderflow.notificationservice.infrastructure.persistence.entity.OrderRecipientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderRecipientJpaRepository extends JpaRepository<OrderRecipientJpaEntity, UUID> {
}
