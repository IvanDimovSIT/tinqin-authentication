package com.tinqinacademy.authentication.api.exception.exceptions;

import com.tinqinacademy.authentication.api.exception.BaseException;
import org.springframework.http.HttpStatus;

public class PromoteException extends BaseException {
    public PromoteException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
