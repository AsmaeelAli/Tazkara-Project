package com.asmaeel.tazkara.identity.model.dto;

import com.asmaeel.tazkara.identity.common.Roles;
import jakarta.validation.constraints.*;

public record RegisterRequestDto(

        @NotBlank(message = "name is required")
        @Pattern(regexp = "^\\p{L}[\\p{L} '\\-]*\\p{L}{1,18}\\p{L}$",message = "name must contain only letters, spaces, hyphens or apostrophes")
        String name,

        @NotBlank(message = "email is required")
        @Email
        @Size(max = 50)
        String email,

        @Size(min = 8, max = 72, message = "password must contain at least 8 characters")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$",message = "password must contain at least one letter and one digit")
        String password,

        @NotNull(message = "role is required and must be ATTENDEE or ORGANIZER")
        Roles role,

        @Pattern(regexp = "^[\\p{L}\\p{N}][\\p{L}\\p{N} ]{1,28}[\\p{L}\\p{N}]$",
                message = "organization name must contain at least 3 characters: letters, digits and spaces")
        String organizationName
) {
}
