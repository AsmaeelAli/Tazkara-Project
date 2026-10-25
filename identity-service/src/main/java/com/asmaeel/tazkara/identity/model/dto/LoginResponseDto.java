package com.asmaeel.tazkara.identity.model.dto;

public record LoginResponseDto(String token, String type, long expiresInMs) {
}
