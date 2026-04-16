package com.brunovirsan.orderflow.orderservice.infrastructure.persistence.adapter;

import com.brunovirsan.orderflow.orderservice.domain.model.Order;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderItem;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderStatus;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers(disabledWithoutDocker = true)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(OrderPersistenceAdapter.class)
class OrderPersistenceAdapterIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("orderdb")
            .withUsername("order")
            .withPassword("order123");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
    }

    @Autowired
    private OrderPersistenceAdapter orderPersistenceAdapter;

    @Test
    void shouldPersistAndLoadOrderWithItems() {
        Order order = Order.create(
                "Joao Silva",
                "joao@email.com",
                List.of(new OrderItem(UUID.randomUUID(), UUID.randomUUID(), "X-Burger", 2, BigDecimal.valueOf(25.90)))
        );

        Order savedOrder = orderPersistenceAdapter.save(order);
        Order loadedOrder = orderPersistenceAdapter.findById(savedOrder.getId()).orElseThrow();

        assertThat(loadedOrder.getItems()).hasSize(1);
        assertThat(loadedOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(loadedOrder.getTotalAmount()).isEqualByComparingTo("51.80");
    }
}
