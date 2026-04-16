package com.brunovirsan.orderflow.kitchenservice.infrastructure.messaging.consumer;

import com.brunovirsan.orderflow.contracts.event.OrderCreatedEvent;
import com.brunovirsan.orderflow.contracts.model.OrderItemPayload;
import com.brunovirsan.orderflow.kitchenservice.application.port.out.KitchenEventPublisher;
import com.brunovirsan.orderflow.kitchenservice.infrastructure.persistence.repository.KitchenTicketJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static java.time.Duration.ofSeconds;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"order.created", "order.cancelled", "order.confirmed"})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.datasource.url=jdbc:h2:mem:kitchenconsumer;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class OrderCreatedConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private KitchenTicketJpaRepository kitchenTicketJpaRepository;

    @MockBean
    private KitchenEventPublisher kitchenEventPublisher;

    @Test
    void shouldCreateKitchenTicketWhenOrderCreatedEventArrives() {
        OrderCreatedEvent event = new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "corr-123",
                Instant.now(),
                "Joao Silva",
                "joao@email.com",
                BigDecimal.valueOf(51.80),
                List.of(new OrderItemPayload(UUID.randomUUID(), "X-Burger", 2, BigDecimal.valueOf(25.90)))
        );
        doNothing().when(kitchenEventPublisher).publishConfirmed(any(), anyString());

        kafkaTemplate.send("order.created", event.orderId().toString(), event);

        await().atMost(ofSeconds(10)).untilAsserted(() -> {
            org.assertj.core.api.Assertions.assertThat(kitchenTicketJpaRepository.findAll()).hasSize(1);
            verify(kitchenEventPublisher).publishConfirmed(any(), anyString());
        });
    }
}
