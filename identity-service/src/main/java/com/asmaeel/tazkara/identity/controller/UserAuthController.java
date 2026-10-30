package com.asmaeel.tazkara.identity.controller;

import com.asmaeel.tazkara.identity.model.dto.LoginRequestDto;
import com.asmaeel.tazkara.identity.model.dto.LoginResponseDto;
import com.asmaeel.tazkara.identity.model.dto.RegisterRequestDto;
import com.asmaeel.tazkara.identity.model.dto.RegisterResponseDto;
import com.asmaeel.tazkara.identity.usecase.RegisterUserService;
import com.asmaeel.tazkara.identity.usecase.LoginUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/users")
public class UserAuthController {
    private final RegisterUserService registerUserService;
    private final LoginUserService loginUserService;

    public UserAuthController(RegisterUserService registerUserService, LoginUserService loginUserService) {
        this.registerUserService = registerUserService;
        this.loginUserService = loginUserService;
    }

    @PostMapping
    public RegisterResponseDto register(@RequestBody @Valid RegisterRequestDto request) {
        return registerUserService.execute(request);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @Valid LoginRequestDto request) {
        return loginUserService.execute(request);
    }
}
