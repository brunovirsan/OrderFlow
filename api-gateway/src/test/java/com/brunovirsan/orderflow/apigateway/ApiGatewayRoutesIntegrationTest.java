package com.brunovirsan.orderflow.apigateway;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import reactor.netty.DisposableServer;
import reactor.netty.http.server.HttpServer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class ApiGatewayRoutesIntegrationTest {

    private static DisposableServer orderServiceStub;
    private static DisposableServer kitchenServiceStub;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeAll
    static void setUpBackends() {
        orderServiceStub = HttpServer.create()
                .port(0)
                .route(routes -> routes.get("/api/v1/orders/123", (request, response) ->
                        response.status(200)
                                .header("Content-Type", "application/json")
                                .sendString(Mono.just("{\"service\":\"order-service\"}"))))
                .bindNow();

        kitchenServiceStub = HttpServer.create()
                .port(0)
                .route(routes -> routes.get("/api/v1/kitchen/tickets", (request, response) ->
                        response.status(200)
                                .header("Content-Type", "application/json")
                                .sendString(Mono.just("{\"service\":\"kitchen-service\"}"))))
                .bindNow();
    }

    @AfterAll
    static void tearDownBackends() {
        if (orderServiceStub != null) {
            orderServiceStub.disposeNow();
        }
        if (kitchenServiceStub != null) {
            kitchenServiceStub.disposeNow();
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("app.services.order-service-uri", () -> "http://localhost:" + orderServiceStub.port());
        registry.add("app.services.kitchen-service-uri", () -> "http://localhost:" + kitchenServiceStub.port());
    }

    @Test
    void shouldRouteOrderRequestsToOrderService() {
        webTestClient.get()
                .uri("/api/v1/orders/123")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.service").isEqualTo("order-service");
    }

    @Test
    void shouldRouteKitchenRequestsToKitchenService() {
        webTestClient.get()
                .uri("/api/v1/kitchen/tickets")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.service").isEqualTo("kitchen-service");
    }

    @Test
    void shouldExposeHealthEndpoint() {
        webTestClient.get()
                .uri("/actuator/health")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("UP");
    }
}
