package com.tinqinacademy.authentication.api.base;

import com.tinqinacademy.authentication.api.errors.Errors;
import io.vavr.control.Either;

public interface OperationProcessor<I extends OperationInput, O extends OperationOutput> {
    Either<Errors, O> process(I input);
}
