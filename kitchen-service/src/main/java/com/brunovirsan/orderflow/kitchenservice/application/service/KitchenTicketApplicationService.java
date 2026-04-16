package com.brunovirsan.orderflow.kitchenservice.application.service;

import com.brunovirsan.orderflow.contracts.event.OrderCancelledEvent;
import com.brunovirsan.orderflow.contracts.event.OrderCreatedEvent;
import com.brunovirsan.orderflow.contracts.observability.CorrelationContext;
import com.brunovirsan.orderflow.kitchenservice.application.port.in.ListKitchenTicketsUseCase;
import com.brunovirsan.orderflow.kitchenservice.application.port.in.UpdateKitchenTicketStatusUseCase;
import com.brunovirsan.orderflow.kitchenservice.application.port.out.KitchenEventPublisher;
import com.brunovirsan.orderflow.kitchenservice.application.port.out.KitchenTicketRepository;
import com.brunovirsan.orderflow.kitchenservice.application.port.out.ProcessedEventRepository;
import com.brunovirsan.orderflow.kitchenservice.domain.exception.KitchenTicketNotFoundException;
import com.brunovirsan.orderflow.kitchenservice.domain.model.KitchenTicket;
import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketItem;
import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketStatus;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class KitchenTicketApplicationService implements ListKitchenTicketsUseCase, UpdateKitchenTicketStatusUseCase {

    private final KitchenTicketRepository kitchenTicketRepository;
    private final KitchenEventPublisher kitchenEventPublisher;
    private final ProcessedEventRepository processedEventRepository;

    public KitchenTicketApplicationService(KitchenTicketRepository kitchenTicketRepository,
                                           KitchenEventPublisher kitchenEventPublisher,
                                           ProcessedEventRepository processedEventRepository) {
        this.kitchenTicketRepository = kitchenTicketRepository;
        this.kitchenEventPublisher = kitchenEventPublisher;
        this.processedEventRepository = processedEventRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<KitchenTicket> list(TicketStatus status) {
        if (status == null) {
            return kitchenTicketRepository.findAll();
        }
        return kitchenTicketRepository.findByStatus(status);
    }

    @Override
    @Transactional
    public KitchenTicket updateStatus(UUID ticketId, TicketStatus status) {
        KitchenTicket ticket = kitchenTicketRepository.findById(ticketId)
                .orElseThrow(() -> new KitchenTicketNotFoundException(ticketId));
        ticket.updateStatus(status);
        KitchenTicket savedTicket = kitchenTicketRepository.save(ticket);

        if (status == TicketStatus.IN_PREPARATION || status == TicketStatus.READY) {
            kitchenEventPublisher.publishStatusChanged(savedTicket, status, currentCorrelationId());
        }

        return savedTicket;
    }

    @Transactional
    public void receiveOrder(OrderCreatedEvent event, String topic) {
        if (processedEventRepository.exists(event.eventId())) {
            return;
        }

        KitchenTicket ticket = KitchenTicket.receive(
                event.orderId(),
                event.customerName(),
                event.items().stream()
                        .map(item -> new TicketItem(UUID.randomUUID(), item.productName(), item.quantity()))
                        .toList()
        );
        KitchenTicket savedTicket = kitchenTicketRepository.save(ticket);
        kitchenEventPublisher.publishConfirmed(savedTicket, event.correlationId());
        processedEventRepository.save(event.eventId(), topic);
    }

    @Transactional
    public void cancelOrder(OrderCancelledEvent event, String topic) {
        if (processedEventRepository.exists(event.eventId())) {
            return;
        }

        kitchenTicketRepository.findByOrderId(event.orderId()).ifPresent(ticket -> {
            ticket.updateStatus(TicketStatus.CANCELLED);
            kitchenTicketRepository.save(ticket);
        });
        processedEventRepository.save(event.eventId(), topic);
    }

    private String currentCorrelationId() {
        String correlationId = MDC.get(CorrelationContext.MDC_KEY);
        return correlationId == null || correlationId.isBlank()
                ? CorrelationContext.generateCorrelationId()
                : correlationId;
    }
}
