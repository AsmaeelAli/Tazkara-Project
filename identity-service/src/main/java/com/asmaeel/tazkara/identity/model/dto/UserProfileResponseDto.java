package com.asmaeel.tazkara.identity.model.dto;

import com.asmaeel.tazkara.identity.common.Roles;

import java.util.UUID;

public record UserProfileResponseDto(
        UUID id,
        String name,
        String email,
        Roles role,
        String organizationName
){}
