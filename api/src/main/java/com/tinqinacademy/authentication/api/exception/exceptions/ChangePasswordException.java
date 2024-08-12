package com.tinqinacademy.authentication.api.exception.exceptions;

import com.tinqinacademy.authentication.api.exception.BaseException;
import org.springframework.http.HttpStatus;

public class ChangePasswordException extends BaseException {

    public ChangePasswordException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
