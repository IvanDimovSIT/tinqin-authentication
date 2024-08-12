package com.tinqinacademy.authentication.api.operations.demote;

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
public class DemoteInput implements OperationInput {
    @JsonIgnore
    @NotEmpty
    private String jwtHeader;
    @NotEmpty
    private String userId;
}
