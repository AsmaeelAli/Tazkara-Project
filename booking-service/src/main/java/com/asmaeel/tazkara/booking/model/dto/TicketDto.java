package com.asmaeel.tazkara.booking.model.dto;

import java.util.UUID;

public record TicketDto(
        UUID id,
        UUID eventId,
        UUID ticketTypeId
){}
