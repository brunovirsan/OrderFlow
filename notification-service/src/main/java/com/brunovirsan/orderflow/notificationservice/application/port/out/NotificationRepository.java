package com.brunovirsan.orderflow.notificationservice.application.port.out;

import com.brunovirsan.orderflow.notificationservice.domain.model.Notification;

import java.util.List;

public interface NotificationRepository {

    Notification save(Notification notification);

    List<Notification> findAll();
}
