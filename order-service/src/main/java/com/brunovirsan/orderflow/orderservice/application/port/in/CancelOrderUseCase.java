package com.brunovirsan.orderflow.orderservice.application.port.in;

import java.util.UUID;

public interface CancelOrderUseCase {

    void cancel(UUID orderId, String reason);
}
