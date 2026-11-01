package com.asmaeel.tazkara.shared.dto;

import java.util.UUID;

public record TicketTypeCountsDto(
        UUID ticketTypeId,
        Integer available,
        Integer held,
        Integer sold
){}
