package com.asmaeel.tazkara.booking.model.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record EventTicketTypeRequestDto(
        @NotNull
        UUID ticketTypeId,

        @NotBlank
        String name,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal price,

        @NotNull
        @Min(1)
        @Max(10000)
        Integer quantity
) {
}
