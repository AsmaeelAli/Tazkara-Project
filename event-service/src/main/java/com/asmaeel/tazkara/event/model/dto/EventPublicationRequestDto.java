package com.asmaeel.tazkara.event.model.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record EventPublicationRequestDto(
        UUID eventId,
        String eventTitle,
        Instant eventStartTime,
        List<EventTicketTypeRequestDto> ticketsType
){}