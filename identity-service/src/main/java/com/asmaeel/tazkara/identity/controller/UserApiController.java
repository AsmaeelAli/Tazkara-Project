package com.asmaeel.tazkara.identity.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserApiController {

    /**
     * for test
     *
     */

    @GetMapping
    @PreAuthorize("hasRole('ORGANIZER')")
    public String user() {
        return "Hello World !";
    }

}
