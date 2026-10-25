package com.asmaeel.tazkara.identity.usecase;

import com.asmaeel.tazkara.identity.common.Normalizer;
import com.asmaeel.tazkara.identity.excptions.BusinessRuleException;
import com.asmaeel.tazkara.identity.excptions.ConflictException;
import com.asmaeel.tazkara.identity.model.dto.RegisterRequestDto;
import com.asmaeel.tazkara.identity.model.dto.RegisterResponseDto;
import com.asmaeel.tazkara.identity.model.entity.OrganizationMapEntity;
import com.asmaeel.tazkara.identity.model.entity.UserEntity;
import com.asmaeel.tazkara.identity.repository.OrganizationMapRepo;
import com.asmaeel.tazkara.identity.repository.UserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateUser {
    private final UserRepo userRepo;
    private final OrganizationMapRepo organizationMapRepo;
    private final PasswordEncoder passwordEncoder;
    private final Normalizer normalizer;

    public CreateUser(UserRepo userRepo, PasswordEncoder passwordEncoder , Normalizer normalizer , OrganizationMapRepo organizationMapRepo) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.normalizer = normalizer;
        this.organizationMapRepo = organizationMapRepo;
    }

    @Transactional
    public RegisterResponseDto execute(RegisterRequestDto request) {
        String normEmail = normalizer.normalize(request.email());
        String normOrgName = normalizer.normalize(request.organizationName());

        if (userRepo.existsByEmail(normEmail)) {
            throw new ConflictException("Email already registered");
        }

        if (normOrgName != null && organizationMapRepo.existsByNormalizedName(normOrgName)) {
            throw new ConflictException("Organization name already taken");
        }

        return registerByRole(request);
    }

    private RegisterResponseDto registerByRole(RegisterRequestDto request) {

        String orgName = switch (request.role()) {
            case ORGANIZER -> registerOrganizer(request);
            case ATTENDEE -> registerAttendee(request);
        };

        return new RegisterResponseDto(
                request.name(),
                request.email(),
                request.role().toString(),
                orgName
        );
    }

    private String registerOrganizer(RegisterRequestDto request) {

        if(request.organizationName() == null) {
            throw new BusinessRuleException("Organization name is required for ORGANIZER role");
        }

        UserEntity user = userRepo.save(createNewUser(request));

        OrganizationMapEntity map = new OrganizationMapEntity(
                user.getId(),
                request.organizationName(),
                normalizer.normalize(request.organizationName())
        );

        organizationMapRepo.save(map);
        return request.organizationName();
    }

    private String registerAttendee(RegisterRequestDto request) {
        if(request.organizationName() != null){
            throw new BusinessRuleException("ATTENDEE role cannot have an organization name");
        }
        userRepo.save(createNewUser(request));
        return null;
    }

    private UserEntity createNewUser(RegisterRequestDto request) {
        return new UserEntity(
              request.name(),
              passwordEncoder.encode(request.password()),
              normalizer.normalize(request.email()),
              request.role()
        );
    }
}

