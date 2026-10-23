package com.asmaeel.tazkara.identity.excptions;

import com.asmaeel.tazkara.identity.model.dto.MessageResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)//401
    public MessageResponse badCredentials() {
        return new MessageResponse("Invalid username or password");
    }

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)//401
    public MessageResponse disabled() {
        return new MessageResponse("Account is disabled");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)//400
    public MessageResponse badRequest(IllegalArgumentException e) {
        return new MessageResponse(e.getMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//403
    public MessageResponse notFound(NoSuchElementException e) {
        return new MessageResponse(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)//400
    public MessageResponse validation(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(f -> f.getField() + " " + f.getDefaultMessage())
                .findFirst()
                .orElse("Invalid request");

        return new MessageResponse(msg);
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)//409
    public MessageResponse handleDataIntegrityViolation(DataIntegrityViolationException e) {
        return new MessageResponse(
                e.getMessage() != null ? e.getMessage() : "A record with the same unique value already exists"
        );
    }
}
