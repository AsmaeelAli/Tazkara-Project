package com.asmaeel.tazkara.booking.model.dto;

import com.asmaeel.tazkara.booking.common.BookingStatus;
import com.asmaeel.tazkara.booking.common.CancellationReason;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookingResponseDto(
        String reference,
        BookingStatus status,
        UUID eventId,
        String eventTitle,
        Instant eventStartTime,
        BigDecimal total,
        List<ItemResponseDto> items,
        Instant holdExpiresAt,
        CancellationReason cancellationReason
) {
}
