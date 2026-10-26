package com.asmaeel.tazkara.shared.exception;

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
        return new MessageResponse("Invalid email or password");
    }

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)//401
    public MessageResponse disabled() {
        return new MessageResponse("Account is disabled");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)// 400
    public MessageResponse handleBadRequest(IllegalArgumentException e) {
        return new MessageResponse("Bad Request");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)//400
    public MessageResponse handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        return new MessageResponse(message);
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)// 403
    public  MessageResponse forbidden(ForbiddenException e) {return new MessageResponse(e.getMessage());}

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)//404
    public MessageResponse notFound(NoSuchElementException e) {
        return new MessageResponse(e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)//409
    public MessageResponse handleDataIntegrityViolation(ConflictException e) {
        return new MessageResponse(e.getMessage());
    }

    @ExceptionHandler(BusinessRuleException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)//422
    public MessageResponse handleBusinessRuleException(BusinessRuleException e) {
        return new MessageResponse(e.getMessage());
    }
}
