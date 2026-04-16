package com.brunovirsan.orderflow.kitchenservice.domain.exception;

public class InvalidTicketStatusException extends RuntimeException {

    public InvalidTicketStatusException(String message) {
        super(message);
    }
}
