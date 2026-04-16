package com.brunovirsan.orderflow.kitchenservice.infrastructure.persistence.adapter;

import com.brunovirsan.orderflow.kitchenservice.application.port.out.ProcessedEventRepository;
import com.brunovirsan.orderflow.kitchenservice.infrastructure.persistence.entity.ProcessedEventJpaEntity;
import com.brunovirsan.orderflow.kitchenservice.infrastructure.persistence.repository.ProcessedEventJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class ProcessedEventPersistenceAdapter implements ProcessedEventRepository {

    private final ProcessedEventJpaRepository processedEventJpaRepository;

    public ProcessedEventPersistenceAdapter(ProcessedEventJpaRepository processedEventJpaRepository) {
        this.processedEventJpaRepository = processedEventJpaRepository;
    }

    @Override
    public boolean exists(UUID eventId) {
        return processedEventJpaRepository.existsById(eventId);
    }

    @Override
    public void save(UUID eventId, String topic) {
        ProcessedEventJpaEntity entity = new ProcessedEventJpaEntity();
        entity.setEventId(eventId);
        entity.setTopic(topic);
        entity.setProcessedAt(LocalDateTime.now());
        processedEventJpaRepository.save(entity);
    }
}
