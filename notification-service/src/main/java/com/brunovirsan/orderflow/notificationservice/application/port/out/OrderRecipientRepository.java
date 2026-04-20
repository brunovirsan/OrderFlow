package com.brunovirsan.orderflow.notificationservice.application.port.out;

import java.util.Optional;
import java.util.UUID;

public interface OrderRecipientRepository {

    void save(UUID orderId, String recipientEmail);

    Optional<String> findRecipientEmail(UUID orderId);
}
