package com.asmaeel.tazkara.booking.model.dto;

import com.asmaeel.tazkara.booking.common.TicketStatus;

import java.util.UUID;

public record TicketStatusCount(UUID ticketTypeId, TicketStatus status, long count) { }
