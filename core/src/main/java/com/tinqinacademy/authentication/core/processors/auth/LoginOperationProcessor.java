package com.tinqinacademy.authentication.core.processors.auth;

import com.tinqinacademy.authentication.api.base.OperationProcessor;
import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.exception.exceptions.InvalidCredentialsException;
import com.tinqinacademy.authentication.api.exception.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.operations.login.LoginInput;
import com.tinqinacademy.authentication.api.operations.login.LoginOperation;
import com.tinqinacademy.authentication.api.operations.login.LoginOutput;
import com.tinqinacademy.authentication.api.operations.register.RegisterOutput;
import com.tinqinacademy.authentication.core.errors.ErrorMapper;
import com.tinqinacademy.authentication.core.processors.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.security.HashingUtil;
import com.tinqinacademy.authentication.core.security.JwtUtil;
import com.tinqinacademy.authentication.persistence.model.User;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoginOperationProcessor extends BaseOperationProcessor implements LoginOperation {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final HashingUtil hashingUtil;

    public LoginOperationProcessor(ConversionService conversionService, ErrorMapper errorMapper,
                                   Validator validator, UserRepository userRepository, JwtUtil jwtUtil,
                                   HashingUtil hashingUtil) {
        super(conversionService, errorMapper, validator);
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.hashingUtil = hashingUtil;
    }

    private User getUser(LoginInput input) {
        User user = userRepository.findByUsername(input.getUsername())
                .orElseThrow(InvalidCredentialsException::new);

        if(!hashingUtil.verifyPassword(input.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return user;
    }

    @Override
    public Either<Errors, LoginOutput> process(LoginInput input) {
        return Try.of(() -> {
                    log.info("Start process input:{}", input);
                    validate(input);

                    User user = getUser(input);
                    String jwtToken = jwtUtil.generateToken(user.getId(), user.getUserRole());

                    log.info("JWT token:{}", jwtToken); //For testing

                    LoginOutput output = LoginOutput.builder()
                            .jwtToken(jwtToken)
                            .build();



                    log.info("End process result:{}", output);

                    return output;
                })
                .toEither()
                .mapLeft(errorMapper::map);
    }
}
