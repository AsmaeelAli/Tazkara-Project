package com.asmaeel.tazkara.event.model.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record PublicEventSummaryResponseDto(
        UUID id,
        String title,
        String city,
        String venue,
        Instant startDateTime,
        List<TicketTypeResponseDto> ticketTypes
){}
