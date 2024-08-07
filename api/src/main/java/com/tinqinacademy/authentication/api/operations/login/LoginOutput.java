package com.tinqinacademy.authentication.api.operations.login;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.authentication.api.base.OperationOutput;
import lombok.*;

@AllArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class LoginOutput implements OperationOutput {
    @JsonIgnore
    private String jwtToken;
}
