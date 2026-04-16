package com.brunovirsan.orderflow.orderservice.application.service;

import com.brunovirsan.orderflow.contracts.event.OrderConfirmedEvent;
import com.brunovirsan.orderflow.contracts.event.OrderInPreparationEvent;
import com.brunovirsan.orderflow.contracts.event.OrderReadyEvent;
import com.brunovirsan.orderflow.orderservice.application.port.out.OrderRepository;
import com.brunovirsan.orderflow.orderservice.application.port.out.ProcessedEventRepository;
import com.brunovirsan.orderflow.orderservice.domain.exception.OrderNotFoundException;
import com.brunovirsan.orderflow.orderservice.domain.model.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderStatusUpdateService {

    private final OrderRepository orderRepository;
    private final ProcessedEventRepository processedEventRepository;

    public OrderStatusUpdateService(OrderRepository orderRepository,
                                    ProcessedEventRepository processedEventRepository) {
        this.orderRepository = orderRepository;
        this.processedEventRepository = processedEventRepository;
    }

    @Transactional
    public void handleOrderConfirmed(OrderConfirmedEvent event, String topic) {
        if (processedEventRepository.exists(event.eventId())) {
            return;
        }

        Order order = loadOrder(event.orderId());
        order.confirm();
        orderRepository.save(order);
        processedEventRepository.save(event.eventId(), topic);
    }

    @Transactional
    public void handleOrderInPreparation(OrderInPreparationEvent event, String topic) {
        if (processedEventRepository.exists(event.eventId())) {
            return;
        }

        Order order = loadOrder(event.orderId());
        order.startPreparation();
        orderRepository.save(order);
        processedEventRepository.save(event.eventId(), topic);
    }

    @Transactional
    public void handleOrderReady(OrderReadyEvent event, String topic) {
        if (processedEventRepository.exists(event.eventId())) {
            return;
        }

        Order order = loadOrder(event.orderId());
        order.markReady();
        orderRepository.save(order);
        processedEventRepository.save(event.eventId(), topic);
    }

    private Order loadOrder(java.util.UUID orderId) {
        return orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
    }
}
