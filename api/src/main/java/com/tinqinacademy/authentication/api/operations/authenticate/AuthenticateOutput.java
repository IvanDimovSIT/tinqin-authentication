package com.tinqinacademy.authentication.api.operations.authenticate;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import com.tinqinacademy.authentication.api.model.enums.UserRole;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class AuthenticateOutput implements OperationOutput {
    private UserRole role;
    private String username;
    private String password;
}
