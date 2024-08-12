package com.tinqinacademy.authentication.api.operations.promote;

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
public class PromoteInput implements OperationInput {
    @JsonIgnore
    @NotEmpty
    private String jwtHeader;
    @NotEmpty
    private String userId;
}
