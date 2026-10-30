package com.asmaeel.tazkara.booking.model.dto;

import com.asmaeel.tazkara.booking.common.BookingStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record BookingResponseDto(
       String reference,
       BookingStatus status,
       UUID eventId,
       String eventTitle,
       Instant eventStartTime,
       List<ItemResponseDto> items
) {
}
