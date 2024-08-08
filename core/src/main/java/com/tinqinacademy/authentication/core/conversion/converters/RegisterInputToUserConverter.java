package com.tinqinacademy.authentication.core.conversion.converters;

import com.tinqinacademy.authentication.api.operations.register.RegisterInput;
import com.tinqinacademy.authentication.core.conversion.BaseConverter;
import com.tinqinacademy.authentication.persistence.model.User;
import com.tinqinacademy.authentication.persistence.model.enums.UserRole;
import org.springframework.stereotype.Component;

@Component
public class RegisterInputToUserConverter extends BaseConverter<RegisterInput, User> {

    @Override
    protected User convertObject(RegisterInput source) {
        User user = User.builder()
                .username(source.getUsername())
                .email(source.getEmail())
                .dateOfBirth(source.getDateOfBirth())
                .firstName(source.getFirstName())
                .lastName(source.getLastName())
                .phoneNumber(source.getPhoneNumber())
                .userRole(UserRole.USER)
                .build();

        return user;
    }
}
