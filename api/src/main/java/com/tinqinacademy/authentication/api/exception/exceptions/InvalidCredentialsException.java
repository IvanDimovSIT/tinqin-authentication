package com.tinqinacademy.authentication.api.exception.exceptions;

import com.tinqinacademy.authentication.api.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends BaseException {
    public InvalidCredentialsException() {
        super("Invalid username or password", HttpStatus.UNAUTHORIZED);
    }
}
