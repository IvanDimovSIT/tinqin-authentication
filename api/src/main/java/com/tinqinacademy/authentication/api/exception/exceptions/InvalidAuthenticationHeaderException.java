package com.tinqinacademy.authentication.api.exception.exceptions;

import com.tinqinacademy.authentication.api.exception.BaseException;
import org.springframework.http.HttpStatus;

public class InvalidAuthenticationHeaderException extends BaseException {
    public InvalidAuthenticationHeaderException() {
        super("Invalid header", HttpStatus.UNAUTHORIZED);
    }
}
