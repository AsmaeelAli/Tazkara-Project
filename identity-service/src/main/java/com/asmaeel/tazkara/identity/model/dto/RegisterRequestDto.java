package com.asmaeel.tazkara.identity.model.dto;

import com.asmaeel.tazkara.identity.common.Roles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record RegisterRequestDto(

        @Schema(example = "Asmaaeel Ali")
        @NotBlank(message = "name is required")
        @Pattern(regexp = "^\\p{L}[\\p{L} '\\-]*\\p{L}{1,18}\\p{L}$", message = "name must contain only letters, spaces, hyphens or apostrophes")
        String name,

        @Schema(example = "asmaeel@gmail.com")
        @NotBlank(message = "email is required")
        @Email
        @Size(max = 50)
        String email,

        @Schema(example = "Password2001")
        @Size(min = 8, max = 72, message = "password must contain at least 8 characters")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "password must contain at least one letter and one digit")
        String password,

        @Schema(example = "ORGANIZER")
        @NotNull(message = "role is required and must be ATTENDEE or ORGANIZER")
        Roles role,

        @Schema(example = "")
        @NotNull
        @Pattern(regexp = "^(?:[\\p{L}\\p{N}][\\p{L}\\p{N} ]{1,28}[\\p{L}\\p{N}]|)$",
                message = "organization name must contain at least 3 characters: letters, digits and spaces")
        String organizationName
) {
}
