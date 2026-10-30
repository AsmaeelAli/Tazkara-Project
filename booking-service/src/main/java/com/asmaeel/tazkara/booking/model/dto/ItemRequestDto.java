package com.asmaeel.tazkara.booking.model.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record ItemRequestDto(

        @NotNull
        UUID ticketTypeId,

        @NotNull
        @Size(min = 1)
        Integer quantity
) {
}
