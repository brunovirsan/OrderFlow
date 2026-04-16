package com.brunovirsan.orderflow.kitchenservice.api.controller;

import com.brunovirsan.orderflow.contracts.event.OrderCreatedEvent;
import com.brunovirsan.orderflow.contracts.model.OrderItemPayload;
import com.brunovirsan.orderflow.kitchenservice.application.port.out.KitchenEventPublisher;
import com.brunovirsan.orderflow.kitchenservice.application.service.KitchenTicketApplicationService;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:kitchencontroller;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.kafka.bootstrap-servers=localhost:9092"
})
class KitchenTicketControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private KitchenTicketApplicationService kitchenTicketApplicationService;

    @MockBean
    private KitchenEventPublisher kitchenEventPublisher;

    @Test
    void shouldListAndUpdateKitchenTicketStatus() throws Exception {
        doNothing().when(kitchenEventPublisher).publishConfirmed(any(), anyString());
        doNothing().when(kitchenEventPublisher).publishStatusChanged(any(), any(), anyString());

        kitchenTicketApplicationService.receiveOrder(new OrderCreatedEvent(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "corr-123",
                Instant.now(),
                "Joao Silva",
                "joao@email.com",
                BigDecimal.valueOf(51.80),
                List.of(new OrderItemPayload(UUID.randomUUID(), "X-Burger", 2, BigDecimal.valueOf(25.90)))
        ), "order.created");

        String responseBody = mockMvc.perform(get("/api/v1/kitchen/tickets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status", is("RECEIVED")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String ticketId = JsonPath.read(responseBody, "$[0].id");

        mockMvc.perform(patch("/api/v1/kitchen/tickets/{id}/status", ticketId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "status": "IN_PREPARATION"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_PREPARATION")));
    }
}
