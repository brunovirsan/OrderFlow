package com.brunovirsan.orderflow.kitchenservice.application.port.out;

import com.brunovirsan.orderflow.kitchenservice.domain.model.KitchenTicket;
import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketStatus;

public interface KitchenEventPublisher {

    void publishConfirmed(KitchenTicket ticket, String correlationId);

    void publishStatusChanged(KitchenTicket ticket, TicketStatus status, String correlationId);
}
