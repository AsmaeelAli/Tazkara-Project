package com.asmaeel.tazkara.event.controller;

import com.asmaeel.tazkara.event.model.dto.TicketTypeResponseDto;
import com.asmaeel.tazkara.event.model.dto.TicketTypeRequestDto;
import com.asmaeel.tazkara.event.usecase.ticket.CreateTicketTypeService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketTypeController {

    private final CreateTicketTypeService createTicketTypeService;

    public TicketTypeController(CreateTicketTypeService createTicketTypeService) {
        this.createTicketTypeService = createTicketTypeService;
    }

    @PostMapping("/{eventId}")
    public TicketTypeResponseDto newTicketType(@PathVariable UUID eventId, @RequestBody @Valid TicketTypeRequestDto request) {
        return createTicketTypeService.execute(eventId, request);
    }
}
