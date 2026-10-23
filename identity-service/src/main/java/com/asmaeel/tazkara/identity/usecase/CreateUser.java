package com.asmaeel.tazkara.identity.usecase;

import com.asmaeel.tazkara.identity.common.Roles;
import com.asmaeel.tazkara.identity.model.dto.RegisterRequestDto;
import com.asmaeel.tazkara.identity.model.dto.RegisterResponseDto;
import com.asmaeel.tazkara.identity.model.entity.UserEntity;
import com.asmaeel.tazkara.identity.repository.UserRepo;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class CreateUser {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public CreateUser(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }


    public RegisterResponseDto execute(RegisterRequestDto request) {

        if(userRepo.existsByEmail(request.email())){
            throw new NoSuchElementException("ff");
        }

        Roles role = (request.organizationName() == null)
                ? Roles.ATTENDEE
                : Roles.ORGANIZER;

        Specification<UserEntity> spec = ((root, query, cb) ->
           cb.or(
                   cb.equal(root.get("organizationName"),request.organizationName()),
                   cb.equal(root.get("email"),request.email())
           ));

    }
}
