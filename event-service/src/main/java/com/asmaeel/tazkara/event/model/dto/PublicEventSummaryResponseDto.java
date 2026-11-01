package com.asmaeel.tazkara.event.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PublicEventSummaryResponseDto(
        UUID eventId,
        String title,
        String city,
        String venue,
        Instant startDateTime,
        List<PublicTicketTypeResponseDto> ticketTypes
) {}
