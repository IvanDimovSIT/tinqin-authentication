package com.tinqinacademy.authentication.api.validation.age;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RegistrationDateOfBirthValidator implements ConstraintValidator<RegistrationDateOfBirth, LocalDate> {
    private static final int MIN_REGISTRATION_AGE = 18;

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        LocalDate now = LocalDate.now();

        return ChronoUnit.YEARS.between(date, now) >= MIN_REGISTRATION_AGE;
    }
}
