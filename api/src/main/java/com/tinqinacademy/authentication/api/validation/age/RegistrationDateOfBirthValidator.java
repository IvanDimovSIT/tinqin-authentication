package com.tinqinacademy.authentication.api.validation.age;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class RegistrationDateOfBirthValidator implements ConstraintValidator<RegistrationDateOfBirth, LocalDate> {
    private static final int MIN_REGISTRATION_AGE = 18;

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext constraintValidatorContext) {
        return date == null || ChronoUnit.YEARS.between(date, LocalDate.now()) >= MIN_REGISTRATION_AGE;
    }
}
