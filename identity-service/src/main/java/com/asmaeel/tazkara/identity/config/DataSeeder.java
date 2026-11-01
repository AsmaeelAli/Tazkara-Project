package com.asmaeel.tazkara.identity.config;

import com.asmaeel.tazkara.identity.common.Normalizer;
import com.asmaeel.tazkara.identity.common.Roles;
import com.asmaeel.tazkara.identity.model.dto.RegisterRequestDto;
import com.asmaeel.tazkara.identity.repository.UserRepo;
import com.asmaeel.tazkara.identity.usecase.RegisterUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private static final String PASSWORD = "Password2001";

    private final RegisterUserService registerUserService;
    private final UserRepo userRepo;
    private final Normalizer normalizer;

    public DataSeeder(
            RegisterUserService registerUserService,
            UserRepo userRepo,
            Normalizer normalizer
    ) {
        this.registerUserService = registerUserService;
        this.userRepo = userRepo;
        this.normalizer = normalizer;
    }

    @Override
    public void run(ApplicationArguments args) {

        seedUser(
                "Asmaeel Ali",
                "asmaeel@gmail.com",
                Roles.ORGANIZER,
                "Tazkara Demo"
        );

        seedUser(
                "Test User",
                "User@tazkara.com",
                Roles.ATTENDEE,
                ""
        );
    }

    private void seedUser(String name, String email, Roles role, String organizationName) {

        String normalizedEmail = normalizer.normalize(email);

        if (userRepo.existsByEmail(normalizedEmail)) {
            log.info("Seed user already exists: {}", normalizedEmail);
            return;
        }

        RegisterRequestDto request = new RegisterRequestDto(
                name,
                email,
                PASSWORD,
                role,
                organizationName
        );

        registerUserService.execute(request);

        log.info("Seed user created: {} ({})", normalizedEmail, role);
    }
}
