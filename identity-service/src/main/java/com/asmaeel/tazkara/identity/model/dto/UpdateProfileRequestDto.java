package com.asmaeel.tazkara.identity.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UpdateProfileRequestDto(
        @NotBlank(message = "name is required")
        @Pattern(regexp = "^\\p{L}[\\p{L} '\\-]*\\p{L}{1,18}\\p{L}$", message = "name must contain only letters, spaces, hyphens or apostrophes")
        String name,

        @NotNull
        @Pattern(regexp = "^(?:[\\p{L}\\p{N}][\\p{L}\\p{N} ]{1,28}[\\p{L}\\p{N}]|)$",
                message = "organization name must contain at least 3 characters: letters, digits and spaces")
        String organizationName
) {
}
