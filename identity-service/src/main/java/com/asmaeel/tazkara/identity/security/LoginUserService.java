package com.asmaeel.tazkara.identity.security;

import com.asmaeel.tazkara.identity.common.Normalizer;
import com.asmaeel.tazkara.identity.model.dto.LoginRequestDto;
import com.asmaeel.tazkara.identity.model.dto.LoginResponseDto;
import com.asmaeel.tazkara.identity.model.entity.UserEntity;
import com.asmaeel.tazkara.identity.repository.UserRepo;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginUserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final Normalizer normalizer;
    private final JwtService jwtService;

    public LoginUserService(UserRepo userRepo, PasswordEncoder passwordEncoder, Normalizer normalizer , JwtService jwtService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.normalizer = normalizer;
        this.jwtService = jwtService;
    }

    public LoginResponseDto execute(LoginRequestDto request) {
        String normEmail = normalizer.normalize(request.email());

        UserEntity user = userRepo.findByEmail(normEmail)
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(user);

        return new LoginResponseDto(token,"Bearer", jwtService.getExpirationMs());
    }
}
