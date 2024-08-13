package com.tinqinacademy.authentication.api.operations.resetpassword;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class ResetPasswordInput implements OperationInput {
    @NotEmpty
    private String recoveryCode;
    @NotEmpty
    @Size(min = 4, max = 32)
    private String newPassword;
}
