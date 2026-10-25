package com.asmaeel.tazkara.shared.security;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class JwtAccessDenied implements AccessDeniedHandler {

    private final SecurityErrorWriter writer;

    public JwtAccessDenied(SecurityErrorWriter writer) {
        this.writer = writer;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {
        writer.write(request, response, HttpStatus.FORBIDDEN,
                "FORBIDDEN", "You do not have permission to access this resource");
    }
}