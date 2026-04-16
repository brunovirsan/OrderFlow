package com.brunovirsan.orderflow.kitchenservice.domain.exception;

import java.util.UUID;

public class KitchenTicketNotFoundException extends RuntimeException {

    public KitchenTicketNotFoundException(UUID ticketId) {
        super("Kitchen ticket with id [%s] not found".formatted(ticketId));
    }
}
