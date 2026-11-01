package com.asmaeel.tazkara.identity.usecase;

import com.asmaeel.tazkara.identity.common.Normalizer;
import com.asmaeel.tazkara.identity.model.dto.UpdateProfileRequestDto;
import com.asmaeel.tazkara.identity.model.dto.UserProfileResponseDto;
import com.asmaeel.tazkara.identity.model.entity.OrganizationMapEntity;
import com.asmaeel.tazkara.identity.model.entity.UserEntity;
import com.asmaeel.tazkara.identity.repository.OrganizationMapRepo;
import com.asmaeel.tazkara.identity.repository.UserRepo;
import com.asmaeel.tazkara.shared.exception.BusinessRuleException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.UUID;

@Service
public class UpdateProfileService {

    private final UserRepo userRepo;
    private final OrganizationMapRepo organizationMapRepo;
    private final Normalizer normalizer;

    public UpdateProfileService(
            UserRepo userRepo,
            OrganizationMapRepo organizationMapRepo,
            Normalizer normalizer
    ) {
        this.userRepo = userRepo;
        this.organizationMapRepo = organizationMapRepo;
        this.normalizer = normalizer;
    }

    @Transactional
    public UserProfileResponseDto execute(UpdateProfileRequestDto request) {

        UUID userId = UUID.fromString(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName()
        );

        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return switch (user.getRole()) {
            case ATTENDEE -> updateAttendeeProfile(user, request);
            case ORGANIZER -> updateOrganizerProfile(user, request);
        };
    }

    private UserProfileResponseDto updateAttendeeProfile(
            UserEntity user,
            UpdateProfileRequestDto request
    ) {

        if (!request.organizationName().isBlank()) {
            throw new BusinessRuleException("Attendees cannot have an organization name");
        }

        user.setName(request.name());
        userRepo.save(user);

        return new UserProfileResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                ""
        );
    }

    private UserProfileResponseDto updateOrganizerProfile(UserEntity user, UpdateProfileRequestDto request) {

        String originalName = request.organizationName();
        String normalizedName = normalizer.normalize(originalName);

        OrganizationMapEntity organization =
                organizationMapRepo.findByUserId(user.getId());

        if (organization == null) {
            throw new NoSuchElementException("Organization not found");
        }

        boolean alreadyExists =
                organizationMapRepo.existsByNormalizedNameAndUserIdNot(normalizedName, user.getId());

        if (alreadyExists) {
            throw new BusinessRuleException("Organization name already exists");
        }

        user.setName(request.name());
        userRepo.save(user);

        organization.setOriginalName(originalName);
        organization.setNormalizedName(normalizedName);
        organizationMapRepo.save(organization);

        return new UserProfileResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                organization.getOriginalName()
        );
    }
}