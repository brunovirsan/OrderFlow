package com.brunovirsan.orderflow.notificationservice.infrastructure.persistence.adapter;

import com.brunovirsan.orderflow.notificationservice.application.port.out.NotificationRepository;
import com.brunovirsan.orderflow.notificationservice.domain.model.Notification;
import com.brunovirsan.orderflow.notificationservice.infrastructure.persistence.entity.NotificationJpaEntity;
import com.brunovirsan.orderflow.notificationservice.infrastructure.persistence.repository.NotificationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class NotificationPersistenceAdapter implements NotificationRepository {

    private final NotificationJpaRepository notificationJpaRepository;

    public NotificationPersistenceAdapter(NotificationJpaRepository notificationJpaRepository) {
        this.notificationJpaRepository = notificationJpaRepository;
    }

    @Override
    public Notification save(Notification notification) {
        NotificationJpaEntity entity = new NotificationJpaEntity();
        entity.setId(notification.getId());
        entity.setOrderId(notification.getOrderId());
        entity.setRecipientEmail(notification.getRecipientEmail());
        entity.setType(notification.getType());
        entity.setMessage(notification.getMessage());
        entity.setSentAt(notification.getSentAt());
        entity.setStatus(notification.getStatus());
        notificationJpaRepository.save(entity);
        return notification;
    }

    @Override
    public List<Notification> findAll() {
        return notificationJpaRepository.findAll().stream()
                .map(entity -> new Notification(entity.getId(), entity.getOrderId(), entity.getRecipientEmail(), entity.getType(), entity.getMessage(), entity.getSentAt(), entity.getStatus()))
                .toList();
    }
}
