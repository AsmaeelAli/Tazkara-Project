package com.asmaeel.tazkara.event.model.dto;

import com.asmaeel.tazkara.event.common.EventStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventResponseDto(
        UUID eventId,
        String title,
        String description,
        String venue,
        String city,
        LocalDateTime startDateTime,
        EventStatus status
) {
}
