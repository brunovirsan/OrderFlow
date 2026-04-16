package com.brunovirsan.orderflow.contracts.observability;

import java.util.UUID;

public final class CorrelationContext {

    public static final String CORRELATION_ID_HEADER = "X-Correlation-Id";
    public static final String MDC_KEY = "correlationId";

    private CorrelationContext() {
    }

    public static String generateCorrelationId() {
        return UUID.randomUUID().toString();
    }
}
