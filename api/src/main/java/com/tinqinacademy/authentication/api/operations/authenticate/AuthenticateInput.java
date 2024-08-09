package com.tinqinacademy.authentication.api.operations.authenticate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tinqinacademy.authentication.api.base.OperationInput;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class AuthenticateInput implements OperationInput {
    @JsonIgnore
    @NotEmpty
    private String jwtHeader;
}
