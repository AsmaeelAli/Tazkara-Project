package com.asmaeel.tazkara.event.model.dto;

import com.asmaeel.tazkara.event.common.EventStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record EventResponseDto(
        UUID eventId,
        String title,
        String description,
        String venue,
        String city,
        Instant startDateTime,
        EventStatus status,
        List<TicketTypeResponseDto> tickets
) {
}
