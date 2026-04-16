package com.brunovirsan.orderflow.kitchenservice.application.port.out;

import java.util.UUID;

public interface ProcessedEventRepository {

    boolean exists(UUID eventId);

    void save(UUID eventId, String topic);
}
