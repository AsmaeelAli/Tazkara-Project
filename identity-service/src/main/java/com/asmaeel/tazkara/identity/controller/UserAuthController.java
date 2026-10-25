package com.asmaeel.tazkara.identity.controller;

import com.asmaeel.tazkara.identity.model.dto.LoginRequestDto;
import com.asmaeel.tazkara.identity.model.dto.LoginResponseDto;
import com.asmaeel.tazkara.identity.model.dto.RegisterRequestDto;
import com.asmaeel.tazkara.identity.model.dto.RegisterResponseDto;
import com.asmaeel.tazkara.identity.usecase.CreateUser;
import com.asmaeel.tazkara.identity.usecase.LoginUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/users")
public class UserAuthController {
    private final CreateUser createUser;
    private final LoginUserService loginUserService;

    public UserAuthController(CreateUser createUser, LoginUserService loginUserService) {
        this.createUser = createUser;
        this.loginUserService = loginUserService;
    }

    @PostMapping
    public RegisterResponseDto register(@RequestBody @Valid RegisterRequestDto request) {
        return createUser.execute(request);
    }

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody @Valid LoginRequestDto request) {
        return loginUserService.execute(request);
    }
}
