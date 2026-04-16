package com.brunovirsan.orderflow.orderservice.application.port.in;

import com.brunovirsan.orderflow.orderservice.domain.model.Order;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface GetOrderUseCase {

    Order findById(UUID orderId);

    List<Order> findByStatus(OrderStatus status);
}
