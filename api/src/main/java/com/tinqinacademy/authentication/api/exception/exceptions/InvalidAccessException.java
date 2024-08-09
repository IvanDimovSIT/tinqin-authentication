package com.tinqinacademy.authentication.api.exception.exceptions;

import com.tinqinacademy.authentication.api.exception.BaseException;
import com.tinqinacademy.authentication.api.model.enums.UserRole;
import org.springframework.http.HttpStatus;

public class InvalidAccessException extends BaseException {
    public InvalidAccessException(UserRole requiredRole) {
        super(String.format("Required role:%s", requiredRole.toString()), HttpStatus.FORBIDDEN);
    }
}
