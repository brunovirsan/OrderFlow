package com.brunovirsan.orderflow.kitchenservice.application.port.in;

import com.brunovirsan.orderflow.kitchenservice.domain.model.KitchenTicket;
import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketStatus;

import java.util.UUID;

public interface UpdateKitchenTicketStatusUseCase {

    KitchenTicket updateStatus(UUID ticketId, TicketStatus status);
}
