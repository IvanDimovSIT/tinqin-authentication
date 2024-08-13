package com.tinqinacademy.authentication.api.operations.register;

import com.tinqinacademy.authentication.api.base.OperationInput;
import com.tinqinacademy.authentication.api.validation.age.RegistrationDateOfBirth;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class RegisterInput implements OperationInput {
    @NotEmpty
    private String firstName;
    @NotEmpty
    private String lastName;
    @NotEmpty
    private String username;
    @NotEmpty
    @Email
    private String email;
    @NotEmpty
    @Size(min = 4, max = 32)
    private String password;
    @NotEmpty
    @Pattern(regexp = "[0-9]{10}")
    private String phoneNumber;
    @NotNull
    @RegistrationDateOfBirth
    private LocalDate dateOfBirth;
}
