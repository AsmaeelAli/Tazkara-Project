package com.asmaeel.tazkara.event.model.dto;

import com.asmaeel.tazkara.event.common.EventStatus;
import jakarta.validation.constraints.NotNull;

public record StatusRequestDto (
        @NotNull
        EventStatus status
){}
