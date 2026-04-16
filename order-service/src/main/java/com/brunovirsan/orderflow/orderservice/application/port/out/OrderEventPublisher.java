package com.brunovirsan.orderflow.orderservice.application.port.out;

import com.brunovirsan.orderflow.orderservice.domain.model.Order;

public interface OrderEventPublisher {

    void publishOrderCreated(Order order, String correlationId);

    void publishOrderCancelled(Order order, String correlationId, String reason);
}
