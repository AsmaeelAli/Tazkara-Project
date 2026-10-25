package com.asmaeel.tazkara.identity.model.dto;

public record RegisterResponseDto(
        String name,
        String email,
        String role,
        String organizationName
) {
}
