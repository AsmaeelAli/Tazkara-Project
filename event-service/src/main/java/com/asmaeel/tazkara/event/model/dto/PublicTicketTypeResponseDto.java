package com.asmaeel.tazkara.event.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PublicTicketTypeResponseDto(
        UUID ticketTypeId,
        String name,
        BigDecimal price,
        Integer available,
        boolean soldOut
) {}
