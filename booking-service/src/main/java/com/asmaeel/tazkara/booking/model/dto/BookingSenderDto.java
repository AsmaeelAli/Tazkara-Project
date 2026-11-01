package com.asmaeel.tazkara.booking.model.dto;

import com.asmaeel.tazkara.booking.common.BookingStatus;

public record BookingSenderDto(
        String attendeeEmail,
        String reference,
        BookingStatus newStatus,
        String eventTitle
){}
