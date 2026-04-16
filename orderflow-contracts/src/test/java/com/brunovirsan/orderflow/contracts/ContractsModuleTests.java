package com.brunovirsan.orderflow.contracts;

import com.brunovirsan.orderflow.contracts.event.OrderCreatedEvent;
import com.brunovirsan.orderflow.contracts.model.OrderItemPayload;
import com.brunovirsan.orderflow.contracts.observability.CorrelationContext;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ContractsModuleTests {

    @Test
    void shouldCreateOrderCreatedEventWithSharedPayloadShape() {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                CorrelationContext.generateCorrelationId(),
                Instant.now(),
                "Joao Silva",
                "joao@email.com",
                BigDecimal.valueOf(51.80),
                List.of(new OrderItemPayload(UUID.randomUUID(), "X-Burger", 2, BigDecimal.valueOf(25.90)))
        );

        assertThat(event.customerEmail()).isEqualTo("joao@email.com");
        assertThat(event.items()).hasSize(1);
        assertThat(event.totalAmount()).isEqualByComparingTo("51.8");
    }

    @Test
    void shouldGenerateCorrelationIdForObservability() {
        String correlationId = CorrelationContext.generateCorrelationId();

        assertThat(correlationId).isNotBlank();
        assertThat(correlationId).hasSize(36);
    }
}
