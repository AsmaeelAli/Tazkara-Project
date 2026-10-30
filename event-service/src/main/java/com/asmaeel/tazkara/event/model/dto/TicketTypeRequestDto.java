package com.asmaeel.tazkara.event.model.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TicketTypeRequestDto(
        @NotBlank
        String name,

        @NotNull
        @DecimalMin("0.00")
        BigDecimal price,

        @NotNull
        @Min(1)
        @Max(10000)
        Integer quantity
) {}
