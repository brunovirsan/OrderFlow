package com.brunovirsan.orderflow.kitchenservice.api.mapper;

import com.brunovirsan.orderflow.kitchenservice.api.dto.KitchenTicketItemResponse;
import com.brunovirsan.orderflow.kitchenservice.api.dto.KitchenTicketResponse;
import com.brunovirsan.orderflow.kitchenservice.domain.model.KitchenTicket;
import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketItem;
import org.springframework.stereotype.Component;

@Component
public class KitchenTicketApiMapper {

    public KitchenTicketResponse toResponse(KitchenTicket ticket) {
        return new KitchenTicketResponse(
                ticket.getId(),
                ticket.getOrderId(),
                ticket.getCustomerName(),
                ticket.getStatus(),
                ticket.getItems().stream().map(this::toItemResponse).toList(),
                ticket.getReceivedAt(),
                ticket.getUpdatedAt()
        );
    }

    private KitchenTicketItemResponse toItemResponse(TicketItem item) {
        return new KitchenTicketItemResponse(item.getId(), item.getProductName(), item.getQuantity());
    }
}
