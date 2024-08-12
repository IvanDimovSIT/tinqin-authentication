package com.tinqinacademy.authentication.core.processors.auth;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.exception.exceptions.*;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateInput;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOperation;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOutput;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordInput;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordOperation;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordOutput;
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
public class ChangePasswordOperationProcessor extends BaseOperationProcessor implements ChangePasswordOperation {
    private final UserRepository userRepository;
    private final AuthenticateOperation authenticateOperation;
    private final HashingUtil hashingUtil;
    private final JwtUtil jwtUtil;

    public ChangePasswordOperationProcessor(ConversionService conversionService, ErrorMapper errorMapper,
                                            Validator validator, UserRepository userRepository,
                                            AuthenticateOperation authenticateOperation, HashingUtil hashingUtil,
                                            JwtUtil jwtUtil) {
        super(conversionService, errorMapper, validator);
        this.userRepository = userRepository;
        this.authenticateOperation = authenticateOperation;
        this.hashingUtil = hashingUtil;
        this.jwtUtil = jwtUtil;
    }

    private void checkAuthentication(String header) {
        AuthenticateInput authenticateInput = AuthenticateInput.builder()
                .jwtHeader(header)
                .build();

        Either<Errors, AuthenticateOutput> output = authenticateOperation.process(authenticateInput);
        if(output.isLeft()) {
            throw new InvalidTokenException("Invalid token");
        }
    }

    private User getUser(ChangePasswordInput input){
        return userRepository.findByEmail(input.getEmail()).orElseThrow(InvalidCredentialsException::new);
    }

    private void checkPasswordsMatch(String password, String hashedPassword) {
        if(!hashingUtil.verifyPassword(password, hashedPassword)){
            throw new InvalidCredentialsException();
        }
    }

    private void changePassword(User user, String newPassword) {
        if(hashingUtil.verifyPassword(newPassword, user.getPassword())) {
            throw new ChangePasswordException("New password cannot be the same as the old password");
        }

        user.setPassword(hashingUtil.hashPassword(newPassword));
        userRepository.save(user);
    }

    private void validateUserId(User user, String jwtHeader){
        String userId = jwtUtil.extractFromHeader(jwtHeader).getId();
        if(!userId.equals(user.getId().toString())){
            throw new InvalidCredentialsException();
        }
    }

    @Override
    public Either<Errors, ChangePasswordOutput> process(ChangePasswordInput input) {
        return Try.of(() -> {
                    log.info("Start process input:{}", input);
                    validate(input);
                    checkAuthentication(input.getJwtHeader());
                    User user = getUser(input);
                    validateUserId(user, input.getJwtHeader());

                    checkPasswordsMatch(input.getOldPassword(), user.getPassword());
                    changePassword(user, input.getNewPassword());

                    ChangePasswordOutput output = ChangePasswordOutput.builder()
                            .build();

                    log.info("End process result:{}", output);

                    return output;
                })
                .toEither()
                .mapLeft(errorMapper::map);
    }
}
