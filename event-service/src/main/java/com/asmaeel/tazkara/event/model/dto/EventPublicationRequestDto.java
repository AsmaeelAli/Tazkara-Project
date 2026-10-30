package com.asmaeel.tazkara.event.model.dto;

import java.util.List;
import java.util.UUID;

public record EventPublicationRequestDto(
        UUID eventId,
        List<EventTicketTypeRequestDto> ticketsType
){}