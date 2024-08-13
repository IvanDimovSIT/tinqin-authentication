package com.tinqinacademy.authentication.api.operations.changepassword;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class ChangePasswordInput implements OperationInput {
    @JsonIgnore
    private String jwtHeader;
    @NotEmpty
    @Size(min = 4, max = 32)
    private String oldPassword;
    @NotEmpty
    @Size(min = 4, max = 32)
    private String newPassword;
    @NotEmpty
    @Email
    private String email;
}
