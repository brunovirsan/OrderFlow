package com.brunovirsan.orderflow.kitchenservice.infrastructure.persistence.adapter;

import com.brunovirsan.orderflow.kitchenservice.application.port.out.KitchenTicketRepository;
import com.brunovirsan.orderflow.kitchenservice.domain.model.KitchenTicket;
import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketItem;
import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketStatus;
import com.brunovirsan.orderflow.kitchenservice.infrastructure.persistence.entity.KitchenTicketJpaEntity;
import com.brunovirsan.orderflow.kitchenservice.infrastructure.persistence.entity.TicketItemJpaEntity;
import com.brunovirsan.orderflow.kitchenservice.infrastructure.persistence.repository.KitchenTicketJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class KitchenTicketPersistenceAdapter implements KitchenTicketRepository {

    private final KitchenTicketJpaRepository kitchenTicketJpaRepository;

    public KitchenTicketPersistenceAdapter(KitchenTicketJpaRepository kitchenTicketJpaRepository) {
        this.kitchenTicketJpaRepository = kitchenTicketJpaRepository;
    }

    @Override
    public KitchenTicket save(KitchenTicket ticket) {
        return toDomain(kitchenTicketJpaRepository.save(toEntity(ticket)));
    }

    @Override
    public Optional<KitchenTicket> findById(UUID ticketId) {
        return kitchenTicketJpaRepository.findById(ticketId).map(this::toDomain);
    }

    @Override
    public Optional<KitchenTicket> findByOrderId(UUID orderId) {
        return kitchenTicketJpaRepository.findByOrderId(orderId).map(this::toDomain);
    }

    @Override
    public List<KitchenTicket> findAll() {
        return kitchenTicketJpaRepository.findAllByOrderByReceivedAtDesc().stream().map(this::toDomain).toList();
    }

    @Override
    public List<KitchenTicket> findByStatus(TicketStatus status) {
        return kitchenTicketJpaRepository.findByStatusOrderByReceivedAtDesc(status).stream().map(this::toDomain).toList();
    }

    private KitchenTicketJpaEntity toEntity(KitchenTicket ticket) {
        KitchenTicketJpaEntity entity = new KitchenTicketJpaEntity();
        entity.setId(ticket.getId());
        entity.setOrderId(ticket.getOrderId());
        entity.setCustomerName(ticket.getCustomerName());
        entity.setStatus(ticket.getStatus());
        entity.setReceivedAt(ticket.getReceivedAt());
        entity.setUpdatedAt(ticket.getUpdatedAt());
        entity.setItems(ticket.getItems().stream().map(item -> toEntity(item, entity)).toList());
        return entity;
    }

    private TicketItemJpaEntity toEntity(TicketItem item, KitchenTicketJpaEntity ticket) {
        TicketItemJpaEntity entity = new TicketItemJpaEntity();
        entity.setId(item.getId());
        entity.setTicket(ticket);
        entity.setProductName(item.getProductName());
        entity.setQuantity(item.getQuantity());
        return entity;
    }

    private KitchenTicket toDomain(KitchenTicketJpaEntity entity) {
        return new KitchenTicket(
                entity.getId(),
                entity.getOrderId(),
                entity.getCustomerName(),
                entity.getItems().stream().map(this::toDomain).toList(),
                entity.getStatus(),
                entity.getReceivedAt(),
                entity.getUpdatedAt()
        );
    }

    private TicketItem toDomain(TicketItemJpaEntity entity) {
        return new TicketItem(entity.getId(), entity.getProductName(), entity.getQuantity());
    }
}
