package com.tinqinacademy.authentication.core.security;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class UserToken {
    private String id;
    private String role;
}
