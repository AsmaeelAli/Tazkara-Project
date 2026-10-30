package com.asmaeel.tazkara.event.model.dto;

import java.math.BigDecimal;

public record TicketResponseDto(
        String name,
        BigDecimal price,
        Integer quantity
){
}
