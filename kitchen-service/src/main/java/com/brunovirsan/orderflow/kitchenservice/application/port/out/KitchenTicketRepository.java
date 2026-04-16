package com.brunovirsan.orderflow.kitchenservice.application.port.out;

import com.brunovirsan.orderflow.kitchenservice.domain.model.KitchenTicket;
import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KitchenTicketRepository {

    KitchenTicket save(KitchenTicket ticket);

    Optional<KitchenTicket> findById(UUID ticketId);

    Optional<KitchenTicket> findByOrderId(UUID orderId);

    List<KitchenTicket> findAll();

    List<KitchenTicket> findByStatus(TicketStatus status);
}
