package com.brunovirsan.orderflow.notificationservice.infrastructure.persistence.adapter;

import com.brunovirsan.orderflow.notificationservice.application.port.out.OrderRecipientRepository;
import com.brunovirsan.orderflow.notificationservice.infrastructure.persistence.entity.OrderRecipientJpaEntity;
import com.brunovirsan.orderflow.notificationservice.infrastructure.persistence.repository.OrderRecipientJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class OrderRecipientPersistenceAdapter implements OrderRecipientRepository {

    private final OrderRecipientJpaRepository orderRecipientJpaRepository;

    public OrderRecipientPersistenceAdapter(OrderRecipientJpaRepository orderRecipientJpaRepository) {
        this.orderRecipientJpaRepository = orderRecipientJpaRepository;
    }

    @Override
    public void save(UUID orderId, String recipientEmail) {
        OrderRecipientJpaEntity entity = new OrderRecipientJpaEntity();
        entity.setOrderId(orderId);
        entity.setRecipientEmail(recipientEmail);
        orderRecipientJpaRepository.save(entity);
    }

    @Override
    public Optional<String> findRecipientEmail(UUID orderId) {
        return orderRecipientJpaRepository.findById(orderId).map(OrderRecipientJpaEntity::getRecipientEmail);
    }
}
