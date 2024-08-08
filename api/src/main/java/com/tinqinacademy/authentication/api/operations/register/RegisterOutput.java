package com.tinqinacademy.authentication.api.operations.register;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class RegisterOutput implements OperationOutput {
    private String id;
}
