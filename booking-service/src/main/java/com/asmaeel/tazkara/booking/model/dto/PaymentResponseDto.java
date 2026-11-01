package com.asmaeel.tazkara.booking.model.dto;

import com.asmaeel.tazkara.booking.common.BookingStatus;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PaymentResponseDto(
        String reference,
        BookingStatus status,
        BigDecimal totalAmount,
        Instant confirmedAt
){}
