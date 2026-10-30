package com.asmaeel.tazkara.event.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record UpdateEventRequestDto(
        @NotBlank
        @Size(max = 50)
        String title,

        @NotBlank
        @Size(max = 300)
        String description,

        @NotBlank
        @Size(max = 30)
        String venue,

        @NotBlank
        @Size(max = 30)
        String city,

        @NotNull
        Instant startDateTime
) {
}
