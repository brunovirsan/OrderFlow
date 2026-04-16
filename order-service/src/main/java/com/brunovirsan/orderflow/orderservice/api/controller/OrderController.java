package com.brunovirsan.orderflow.orderservice.api.controller;

import com.brunovirsan.orderflow.orderservice.api.dto.CancelOrderRequest;
import com.brunovirsan.orderflow.orderservice.api.dto.CreateOrderRequest;
import com.brunovirsan.orderflow.orderservice.api.dto.OrderResponse;
import com.brunovirsan.orderflow.orderservice.api.mapper.OrderApiMapper;
import com.brunovirsan.orderflow.orderservice.application.port.in.CancelOrderUseCase;
import com.brunovirsan.orderflow.orderservice.application.port.in.CreateOrderUseCase;
import com.brunovirsan.orderflow.orderservice.application.port.in.GetOrderUseCase;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderStatus;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final CreateOrderUseCase createOrderUseCase;
    private final GetOrderUseCase getOrderUseCase;
    private final CancelOrderUseCase cancelOrderUseCase;
    private final OrderApiMapper orderApiMapper;

    public OrderController(CreateOrderUseCase createOrderUseCase,
                           GetOrderUseCase getOrderUseCase,
                           CancelOrderUseCase cancelOrderUseCase,
                           OrderApiMapper orderApiMapper) {
        this.createOrderUseCase = createOrderUseCase;
        this.getOrderUseCase = getOrderUseCase;
        this.cancelOrderUseCase = cancelOrderUseCase;
        this.orderApiMapper = orderApiMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return orderApiMapper.toResponse(createOrderUseCase.create(orderApiMapper.toCommand(request)));
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable UUID id) {
        return orderApiMapper.toResponse(getOrderUseCase.findById(id));
    }

    @GetMapping
    public List<OrderResponse> findByStatus(@RequestParam(required = false) OrderStatus status) {
        return getOrderUseCase.findByStatus(status).stream().map(orderApiMapper::toResponse).toList();
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable UUID id, @Valid @RequestBody CancelOrderRequest request) {
        cancelOrderUseCase.cancel(id, request.reason());
    }
}
