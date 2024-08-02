package com.tinqinacademy.authentication.core.errors;


import com.tinqinacademy.authentication.api.errors.Errors;

public interface ErrorMapper {
    Errors map(Throwable throwable);
}
