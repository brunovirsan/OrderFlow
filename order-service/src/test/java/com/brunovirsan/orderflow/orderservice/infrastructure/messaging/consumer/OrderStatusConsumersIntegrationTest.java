package com.brunovirsan.orderflow.orderservice.infrastructure.messaging.consumer;

import com.brunovirsan.orderflow.contracts.event.OrderConfirmedEvent;
import com.brunovirsan.orderflow.orderservice.application.port.out.OrderEventPublisher;
import com.brunovirsan.orderflow.orderservice.infrastructure.persistence.repository.OrderJpaRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.UUID;

import static java.time.Duration.ofSeconds;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka(partitions = 1, topics = {"order.confirmed", "order.in-preparation", "order.ready"})
@TestPropertySource(properties = {
        "spring.kafka.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.datasource.url=jdbc:h2:mem:orderconsumers;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password="
})
class OrderStatusConsumersIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @MockBean
    private OrderEventPublisher orderEventPublisher;

    @Test
    void shouldUpdateOrderStatusWhenKitchenConfirmsOrder() throws Exception {
        doNothing().when(orderEventPublisher).publishOrderCreated(any(), anyString());

        String responseBody = mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "customerName": "Joao Silva",
                                  "customerEmail": "joao@email.com",
                                  "items": [
                                    {
                                      "productId": "6ab56f1d-7d2e-446c-a6b2-1d57e160bfdd",
                                      "productName": "X-Burger",
                                      "quantity": 2,
                                      "unitPrice": 25.90
                                    }
                                  ]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String orderId = JsonPath.read(responseBody, "$.id");

        kafkaTemplate.send("order.confirmed", orderId, new OrderConfirmedEvent(
                UUID.randomUUID(),
                UUID.fromString(orderId),
                "corr-123",
                Instant.now(),
                UUID.randomUUID()
        ));

        await().atMost(ofSeconds(10)).untilAsserted(() ->
                org.assertj.core.api.Assertions.assertThat(
                        orderJpaRepository.findById(UUID.fromString(orderId)).orElseThrow().getStatus().name()
                ).isEqualTo("CONFIRMED"));
    }
}
