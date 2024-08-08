package com.tinqinacademy.authentication.api.operations.changepassword;

import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class ChangePasswordInput implements OperationInput {
    @NotEmpty
    private String oldPassword;
    @NotEmpty
    private String newPassword;
    @NotEmpty
    @Email
    private String email;
}
