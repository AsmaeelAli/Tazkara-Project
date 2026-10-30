package com.asmaeel.tazkara.booking.model.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record BookingRequestDto (

        @NotNull
        UUID eventId,

        @NotEmpty
        @Valid
        @Size(max = 5)
        List<ItemRequestDto> items
){
}
