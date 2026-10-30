package com.asmaeel.tazkara.event.model.dto;

import java.math.BigDecimal;

public record TicketTypeResponseDto(
        String name,
        BigDecimal price,
        Integer quantity
){
}
