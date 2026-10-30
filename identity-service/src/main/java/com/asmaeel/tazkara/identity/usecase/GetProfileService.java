package com.asmaeel.tazkara.identity.usecase;

import com.asmaeel.tazkara.identity.model.dto.UserProfileResponseDto;
import com.asmaeel.tazkara.identity.model.entity.OrganizationMapEntity;
import com.asmaeel.tazkara.identity.model.entity.UserEntity;
import com.asmaeel.tazkara.identity.repository.OrganizationMapRepo;
import com.asmaeel.tazkara.identity.repository.UserRepo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class GetProfileService {

    private final UserRepo userRepo;
    private final OrganizationMapRepo organizationMapRepo;

    public GetProfileService(
            OrganizationMapRepo organizationMapRepo,
            UserRepo userRepo
    ) {
        this.userRepo = userRepo;
        this.organizationMapRepo = organizationMapRepo;
    }

    public UserProfileResponseDto execute() {

        UUID userId = UUID.fromString(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        OrganizationMapEntity org = organizationMapRepo.findByUserId(userId);

        String orgName = org != null ? org.getOriginalName() : "";

        return new UserProfileResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                orgName
        );
    }
}