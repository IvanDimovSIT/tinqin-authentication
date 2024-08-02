package com.tinqinacaademy.authentication.rest.controllers;


import com.tinqinacademy.authentication.api.base.OperationOutput;
import com.tinqinacademy.authentication.api.errors.Errors;
import io.vavr.control.Either;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public abstract class BaseController {
    protected <T extends OperationOutput> ResponseEntity<?> mapToResponseEntity(Either<Errors, T> either, HttpStatus status) {
        return either.isRight()?
                new ResponseEntity<>(either.get(), status):
                new ResponseEntity<>(either.getLeft().getErrorInfos(), either.getLeft().getStatus());
    }
}
