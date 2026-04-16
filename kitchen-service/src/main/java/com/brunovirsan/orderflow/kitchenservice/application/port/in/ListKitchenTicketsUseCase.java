package com.brunovirsan.orderflow.kitchenservice.application.port.in;

import com.brunovirsan.orderflow.kitchenservice.domain.model.KitchenTicket;
import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketStatus;

import java.util.List;

public interface ListKitchenTicketsUseCase {

    List<KitchenTicket> list(TicketStatus status);
}
