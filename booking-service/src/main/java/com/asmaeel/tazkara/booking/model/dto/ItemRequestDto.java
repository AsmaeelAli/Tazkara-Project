package com.asmaeel.tazkara.booking.model.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ItemRequestDto(

        @NotNull
        UUID ticketTypeId,

        @NotNull
        @Min(1)
        @Max(6)
        Integer quantity
) {
}
