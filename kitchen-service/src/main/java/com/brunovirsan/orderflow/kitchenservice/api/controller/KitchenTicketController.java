package com.brunovirsan.orderflow.kitchenservice.api.controller;

import com.brunovirsan.orderflow.kitchenservice.api.dto.KitchenTicketResponse;
import com.brunovirsan.orderflow.kitchenservice.api.dto.UpdateKitchenTicketStatusRequest;
import com.brunovirsan.orderflow.kitchenservice.api.mapper.KitchenTicketApiMapper;
import com.brunovirsan.orderflow.kitchenservice.application.port.in.ListKitchenTicketsUseCase;
import com.brunovirsan.orderflow.kitchenservice.application.port.in.UpdateKitchenTicketStatusUseCase;
import com.brunovirsan.orderflow.kitchenservice.domain.model.TicketStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/kitchen/tickets")
public class KitchenTicketController {

    private final ListKitchenTicketsUseCase listKitchenTicketsUseCase;
    private final UpdateKitchenTicketStatusUseCase updateKitchenTicketStatusUseCase;
    private final KitchenTicketApiMapper kitchenTicketApiMapper;

    public KitchenTicketController(ListKitchenTicketsUseCase listKitchenTicketsUseCase,
                                   UpdateKitchenTicketStatusUseCase updateKitchenTicketStatusUseCase,
                                   KitchenTicketApiMapper kitchenTicketApiMapper) {
        this.listKitchenTicketsUseCase = listKitchenTicketsUseCase;
        this.updateKitchenTicketStatusUseCase = updateKitchenTicketStatusUseCase;
        this.kitchenTicketApiMapper = kitchenTicketApiMapper;
    }

    @GetMapping
    public List<KitchenTicketResponse> list(@RequestParam(required = false) TicketStatus status) {
        return listKitchenTicketsUseCase.list(status).stream().map(kitchenTicketApiMapper::toResponse).toList();
    }

    @PatchMapping("/{id}/status")
    public KitchenTicketResponse updateStatus(@PathVariable UUID id,
                                              @Valid @RequestBody UpdateKitchenTicketStatusRequest request) {
        return kitchenTicketApiMapper.toResponse(updateKitchenTicketStatusUseCase.updateStatus(id, request.status()));
    }
}
