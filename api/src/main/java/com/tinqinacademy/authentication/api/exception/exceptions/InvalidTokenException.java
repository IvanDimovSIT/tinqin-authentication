package com.tinqinacademy.authentication.api.exception.exceptions;

import com.tinqinacademy.authentication.api.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BaseException {
    public InvalidTokenException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
