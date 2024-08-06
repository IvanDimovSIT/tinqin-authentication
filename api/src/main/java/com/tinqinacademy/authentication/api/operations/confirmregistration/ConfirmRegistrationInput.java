package com.tinqinacademy.authentication.api.operations.confirmregistration;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class ConfirmRegistrationInput implements OperationInput {
    @NotEmpty
    private String confirmationCode;
}
