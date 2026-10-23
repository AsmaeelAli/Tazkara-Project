package com.asmaeel.tazkara.identity.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequestDto(

        @NotBlank
        @Size(min = 3, max = 30)
        @Pattern(
                regexp = "^[\\p{L}][\\p{L} ]*$",
                message = "Name must contain only letters and spaces, and must start with a letter"
        )
        String name,

        @NotBlank
        @Email
        String email,

        @NotBlank
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password,

        @Pattern(
                regexp = "^[a-zA-Z0-9]{1,30}$",
                message = "Organization name must contain only letters and numbers, without spaces"
        )
        String organizationName
) {
}
