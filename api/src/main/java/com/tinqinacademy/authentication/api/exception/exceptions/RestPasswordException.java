package com.tinqinacademy.authentication.api.exception.exceptions;

import com.tinqinacademy.authentication.api.exception.BaseException;
import org.springframework.http.HttpStatus;

public class RestPasswordException extends BaseException {
    public RestPasswordException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
