package com.brunovirsan.orderflow.orderservice.infrastructure.messaging.producer;

import com.brunovirsan.orderflow.contracts.event.OrderCreatedEvent;
import com.brunovirsan.orderflow.orderservice.domain.model.Order;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderItem;
import com.brunovirsan.orderflow.orderservice.infrastructure.config.KafkaTopicsProperties;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"order.created", "order.cancelled"})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.datasource.url=jdbc:h2:mem:orderkafkatest;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.flyway.enabled=false"
})
class OrderKafkaProducerTest {

    @Autowired
    private OrderKafkaProducer orderKafkaProducer;

    @Autowired
    private KafkaTopicsProperties kafkaTopicsProperties;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    private Consumer<String, OrderCreatedEvent> consumer;

    @AfterEach
    void tearDown() {
        if (consumer != null) {
            consumer.close();
        }
    }

    @Test
    void shouldPublishOrderCreatedEvent() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("order-service-test", "false", embeddedKafkaBroker);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, OrderCreatedEvent.class.getName());
        ConsumerFactory<String, OrderCreatedEvent> consumerFactory = new DefaultKafkaConsumerFactory<>(props);
        consumer = consumerFactory.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, kafkaTopicsProperties.orderCreated());
        consumer.poll(Duration.ofSeconds(1));

        Order order = Order.create(
                "Joao Silva",
                "joao@email.com",
                List.of(new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "X-Burger", 2, BigDecimal.valueOf(25.90)))
        );

        orderKafkaProducer.publishOrderCreated(order, "corr-123");

        ConsumerRecord<String, OrderCreatedEvent> record =
                KafkaTestUtils.getSingleRecord(consumer, kafkaTopicsProperties.orderCreated(), Duration.ofSeconds(10));
        assertThat(record.value().orderId()).isEqualTo(order.getId());
        assertThat(record.value().correlationId()).isEqualTo("corr-123");
    }
}
