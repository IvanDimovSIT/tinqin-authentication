package com.tinqinacademy.authentication.api.operations.authenticate;

import com.tinqinacademy.authentication.api.base.OperationOutput;
import com.tinqinacademy.authentication.api.model.enums.UserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
public class AuthenticateOutput implements OperationOutput, UserDetails {
    private UserRole role;
    private String username;
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(role);
    }
}
