package com.asmaeel.tazkara.booking.controller;

import com.asmaeel.tazkara.booking.model.dto.EventPublicationRequestDto;
import com.asmaeel.tazkara.booking.usecase.CreateTicketsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal")
public class InternalTicketController {

    private final CreateTicketsService createTicketsService;

    public InternalTicketController(CreateTicketsService createTicketsService) {
        this.createTicketsService = createTicketsService;
    }

    @PostMapping("/tickets")
    public ResponseEntity<String> createTickets(@RequestBody @Valid EventPublicationRequestDto request) {
        createTicketsService.execute(request);
        return ResponseEntity.ok().build();
    }
}
