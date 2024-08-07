package com.tinqinacademy.authentication.core.processors.auth;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.exception.exceptions.InvalidTokenException;
import com.tinqinacademy.authentication.api.exception.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.model.enums.UserRole;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateInput;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOperation;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOutput;
import com.tinqinacademy.authentication.core.errors.ErrorMapper;
import com.tinqinacademy.authentication.core.processors.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.security.JwtUtil;
import com.tinqinacademy.authentication.core.security.UserToken;
import com.tinqinacademy.authentication.persistence.model.User;
import com.tinqinacademy.authentication.persistence.repository.BlacklistedTokenRepository;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class AuthenticateOperationProcessor extends BaseOperationProcessor implements AuthenticateOperation {
    private final JwtUtil jwtUtil;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    private final UserRepository userRepository;

    public AuthenticateOperationProcessor(ConversionService conversionService, ErrorMapper errorMapper,
                                          Validator validator, JwtUtil jwtUtil,
                                          BlacklistedTokenRepository blacklistedTokenRepository,
                                          UserRepository userRepository) {
        super(conversionService, errorMapper, validator);
        this.jwtUtil = jwtUtil;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
        this.userRepository = userRepository;
    }

    private void validateToken(String token) {
        if (blacklistedTokenRepository.existsByToken(token) ||
                !jwtUtil.validateToken(token)) {
            throw new InvalidTokenException(String.format("Invalid token: %s", token));
        }
    }

    private User getUserFromToken(String token) {
        UserToken userToken = jwtUtil.extract(token);
        User user = userRepository.findById(UUID.fromString(userToken.getId()))
                .orElseThrow(() -> new NotFoundException(String.format("User not found: %s", userToken.getId())));

        if(!user.getUserRole().toString().equals(userToken.getRole().toString())) {
            throw new InvalidTokenException("Roles do not match");
        }
        if (userToken.getRole().toString().equals(UserRole.UNKNOWN.toString())) {
            throw new InvalidTokenException("Invalid user role");
        }

        return user;
    }

    @Override
    public Either<Errors, AuthenticateOutput> process(AuthenticateInput input) {
        return Try.of(() -> {
                    log.info("Start process input:{}", input);
                    validate(input);
                    validateToken(input.getJwtToken());
                    User user = getUserFromToken(input.getJwtToken());

                    AuthenticateOutput output = AuthenticateOutput.builder()
                            .role(UserRole.getCode(user.getUserRole().toString()))
                            .username(user.getUsername())
                            .password(user.getPassword())
                            .build();

                    log.info("End process result:{}", output);

                    return output;
                })
                .toEither()
                .mapLeft(errorMapper::map);
    }
}
