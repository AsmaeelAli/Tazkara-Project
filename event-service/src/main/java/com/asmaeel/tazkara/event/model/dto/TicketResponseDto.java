package com.asmaeel.tazkara.event.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TicketResponseDto(
        UUID eventId,
        String name,
        BigDecimal price,
        Integer quantity
){
}
