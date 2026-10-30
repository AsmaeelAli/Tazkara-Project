package com.asmaeel.tazkara.identity.controller;

import com.asmaeel.tazkara.identity.model.dto.UpdateProfileRequestDto;
import com.asmaeel.tazkara.identity.model.dto.UserProfileResponseDto;
import com.asmaeel.tazkara.identity.usecase.GetProfileService;
import com.asmaeel.tazkara.identity.usecase.UpdateProfileService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
public class UserApiController {

    private final GetProfileService getProfileService;
    private final UpdateProfileService updateProfileService;

    public UserApiController(UpdateProfileService updateProfileService, GetProfileService getProfileService) {
        this.getProfileService = getProfileService;
        this.updateProfileService = updateProfileService;
    }

    @GetMapping()
    public UserProfileResponseDto me(){
        return getProfileService.execute();
    }


    @PutMapping()
    public UserProfileResponseDto update(@RequestBody @Valid UpdateProfileRequestDto request){
        return updateProfileService.execute(request);
    }



}
