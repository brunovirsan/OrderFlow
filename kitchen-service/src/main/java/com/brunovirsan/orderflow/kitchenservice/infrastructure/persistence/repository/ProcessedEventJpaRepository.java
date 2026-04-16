package com.brunovirsan.orderflow.kitchenservice.infrastructure.persistence.repository;

import com.brunovirsan.orderflow.kitchenservice.infrastructure.persistence.entity.ProcessedEventJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventJpaRepository extends JpaRepository<ProcessedEventJpaEntity, UUID> {
}
