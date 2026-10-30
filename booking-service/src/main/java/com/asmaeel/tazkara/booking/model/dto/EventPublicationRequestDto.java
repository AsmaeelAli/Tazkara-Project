package com.asmaeel.tazkara.booking.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record EventPublicationRequestDto(
        @NotNull
        UUID eventId,

        @NotNull
        @Valid
        List<EventTicketTypeRequestDto> ticketsType
){}