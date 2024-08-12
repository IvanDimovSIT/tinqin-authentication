package com.tinqinacademy.authentication.api.validation.age;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD,TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = RegistrationDateOfBirthValidator.class)
public @interface RegistrationDateOfBirth {
    String message() default "User needs to be at least 18 years old";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
