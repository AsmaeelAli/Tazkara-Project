package com.asmaeel.tazkara.event.model.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PublicTicketTypeResponseDto(
        UUID ticketTypeId,
        String name,
        BigDecimal price,
        Integer available,
        boolean soldOut
) {}
