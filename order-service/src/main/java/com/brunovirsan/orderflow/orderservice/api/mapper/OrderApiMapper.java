package com.brunovirsan.orderflow.orderservice.api.mapper;

import com.brunovirsan.orderflow.orderservice.api.dto.CreateOrderRequest;
import com.brunovirsan.orderflow.orderservice.api.dto.OrderItemResponse;
import com.brunovirsan.orderflow.orderservice.api.dto.OrderResponse;
import com.brunovirsan.orderflow.orderservice.application.port.in.CreateOrderCommand;
import com.brunovirsan.orderflow.orderservice.domain.model.Order;
import com.brunovirsan.orderflow.orderservice.domain.model.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderApiMapper {

    public CreateOrderCommand toCommand(CreateOrderRequest request) {
        return new CreateOrderCommand(
                request.customerName(),
                request.customerEmail(),
                request.items().stream()
                        .map(item -> new CreateOrderCommand.CreateOrderItemCommand(
                                item.productId(),
                                item.productName(),
                                item.quantity(),
                                item.unitPrice()))
                        .toList()
        );
    }

    public OrderResponse toResponse(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerName(),
                order.getCustomerEmail(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems().stream().map(this::toItemResponse).toList(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice()
        );
    }
}
