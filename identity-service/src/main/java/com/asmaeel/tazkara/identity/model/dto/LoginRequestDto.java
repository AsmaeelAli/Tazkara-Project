package com.asmaeel.tazkara.identity.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record LoginRequestDto(

        @NotBlank(message = "email is required")
        @Email
        @Size(max = 50)
        String email,


        @Size(min = 8, max = 72, message = "Invalid email or password")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Invalid email or password")
        String password
) {
}
