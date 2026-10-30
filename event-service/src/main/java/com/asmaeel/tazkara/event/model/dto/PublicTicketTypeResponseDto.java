package com.asmaeel.tazkara.event.model.dto;

import java.math.BigDecimal;

public record PublicTicketTypeResponseDto(
        String name,
        BigDecimal price,
        Integer available,
        Integer soldOut
){
}
