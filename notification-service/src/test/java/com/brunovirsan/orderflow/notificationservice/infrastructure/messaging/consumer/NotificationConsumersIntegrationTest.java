package com.brunovirsan.orderflow.notificationservice.infrastructure.messaging.consumer;

import com.brunovirsan.orderflow.contracts.event.OrderCreatedEvent;
import com.brunovirsan.orderflow.contracts.event.OrderReadyEvent;
import com.brunovirsan.orderflow.contracts.model.OrderItemPayload;
import com.brunovirsan.orderflow.notificationservice.infrastructure.persistence.repository.NotificationJpaRepository;
import com.brunovirsan.orderflow.notificationservice.infrastructure.persistence.repository.OrderRecipientJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static java.time.Duration.ofSeconds;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"order.created", "order.confirmed", "order.ready", "order.cancelled"})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.key-serializer=org.apache.kafka.common.serialization.StringSerializer",
        "spring.kafka.producer.value-serializer=org.springframework.kafka.support.serializer.JsonSerializer",
        "spring.datasource.url=jdbc:h2:mem:notificationconsumer;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class NotificationConsumersIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private NotificationJpaRepository notificationJpaRepository;

    @Autowired
    private OrderRecipientJpaRepository orderRecipientJpaRepository;

    @Test
    void shouldPersistNotificationAfterRecipientProjectionAndReadyEvent() {
        UUID orderId = UUID.randomUUID();
        kafkaTemplate.send("order.created", orderId.toString(), new OrderCreatedEvent(
                UUID.randomUUID(),
                orderId,
                "corr-123",
                Instant.now(),
                "Joao Silva",
                "joao@email.com",
                BigDecimal.valueOf(51.80),
                List.of(new OrderItemPayload(UUID.randomUUID(), "X-Burger", 2, BigDecimal.valueOf(25.90)))
        ));

        await().atMost(ofSeconds(10)).untilAsserted(() ->
                org.assertj.core.api.Assertions.assertThat(orderRecipientJpaRepository.findById(orderId)).isPresent());

        kafkaTemplate.send("order.ready", orderId.toString(), new OrderReadyEvent(
                UUID.randomUUID(),
                orderId,
                "corr-123",
                Instant.now()
        ));

        await().atMost(ofSeconds(10)).untilAsserted(() ->
                org.assertj.core.api.Assertions.assertThat(notificationJpaRepository.findAll()).hasSize(1));
    }
}
