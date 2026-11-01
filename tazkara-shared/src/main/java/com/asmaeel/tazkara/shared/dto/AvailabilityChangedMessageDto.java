package com.asmaeel.tazkara.shared.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record AvailabilityChangedMessageDto(
        UUID eventId,
        Instant sentAt,
        List<TicketTypeCountsDto> ticketTypes
) {
}
