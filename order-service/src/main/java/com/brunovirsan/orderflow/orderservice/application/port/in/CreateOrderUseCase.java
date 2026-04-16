package com.brunovirsan.orderflow.orderservice.application.port.in;

import com.brunovirsan.orderflow.orderservice.domain.model.Order;

public interface CreateOrderUseCase {

    Order create(CreateOrderCommand command);
}
