package com.asmaeel.tazkara.booking.model.dto;

import com.asmaeel.tazkara.booking.common.PaymentMethod;
import jakarta.validation.constraints.NotNull;

public record PaymentRequestDto(
        @NotNull
        PaymentMethod paymentMethod
){}
