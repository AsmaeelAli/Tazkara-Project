package com.asmaeel.tazkara.event.controller;

import com.asmaeel.tazkara.event.model.dto.TicketResponseDto;
import com.asmaeel.tazkara.event.model.dto.TicketRequestDto;
import com.asmaeel.tazkara.event.usecase.ticket.CreateTicketType;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketTypeController {

    private final CreateTicketType createTicketType;

    public TicketTypeController(CreateTicketType createTicketType) {
        this.createTicketType = createTicketType;
    }

    @PostMapping("/{eventId}")
    public TicketResponseDto newTicketType(@PathVariable UUID eventId, @RequestBody @Valid TicketRequestDto request) {
        return createTicketType.execute(eventId, request);
    }
}
