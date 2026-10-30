package com.asmaeel.tazkara.booking.model.dto;

import java.math.BigDecimal;

public record ItemResponseDto(
        String ticketTypeName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
){}
