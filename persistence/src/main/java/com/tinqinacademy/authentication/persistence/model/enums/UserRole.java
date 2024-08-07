package com.tinqinacademy.authentication.persistence.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Arrays;

public enum UserRole implements GrantedAuthority {
    USER("user"),
    ADMIN("admin"),
    UNKNOWN("");

    private final String code;

    UserRole(String code) {
        this.code = code;
    }

    @JsonCreator
    public static UserRole getCode(String code) {
        return Arrays.stream(UserRole.values())
                .filter(userRole -> userRole.code.equals(code))
                .findFirst()
                .orElse(UNKNOWN);
    }

    @JsonValue
    public String toString() {
        return code;
    }

    @Override
    public String getAuthority() {
        return toString();
    }
}