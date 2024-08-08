package com.tinqinacademy.authentication.core.processors.auth;

import com.tinqinacademy.authentication.api.base.OperationProcessor;
import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.exception.exceptions.InvalidAccessException;
import com.tinqinacademy.authentication.api.exception.exceptions.InvalidTokenException;
import com.tinqinacademy.authentication.api.exception.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.exception.exceptions.PromoteException;
import com.tinqinacademy.authentication.api.model.enums.UserRole;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateInput;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOperation;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOutput;
import com.tinqinacademy.authentication.api.operations.login.LoginOutput;
import com.tinqinacademy.authentication.api.operations.promote.PromoteInput;
import com.tinqinacademy.authentication.api.operations.promote.PromoteOperation;
import com.tinqinacademy.authentication.api.operations.promote.PromoteOutput;
import com.tinqinacademy.authentication.core.errors.ErrorMapper;
import com.tinqinacademy.authentication.core.processors.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.security.JwtUtil;
import com.tinqinacademy.authentication.core.security.UserToken;
import com.tinqinacademy.authentication.persistence.model.User;
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
public class PromoteOperationProcessor extends BaseOperationProcessor implements PromoteOperation {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthenticateOperation authenticateOperation;

    public PromoteOperationProcessor(ConversionService conversionService, ErrorMapper errorMapper,
                                     Validator validator, JwtUtil jwtUtil, UserRepository userRepository,
                                     AuthenticateOperation authenticateOperation) {
        super(conversionService, errorMapper, validator);
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.authenticateOperation = authenticateOperation;
    }

    private AuthenticateOutput getAuthentication(String header) {
        String token = jwtUtil.getTokenFromHeader(header);
        AuthenticateInput authenticateInput = AuthenticateInput.builder()
                .jwtToken(token)
                .build();

        Either<Errors, AuthenticateOutput> output = authenticateOperation.process(authenticateInput);
        if(output.isLeft()) {
            throw new InvalidTokenException("Invalid token");
        }

        return output.get();
    }

    private User getUserToPromote(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(
                () -> new NotFoundException(String.format("User with id:%s", userId)));

        if(user.getUserRole().toString().equals(UserRole.ADMIN.toString())) {
            throw new PromoteException("User is already admin");
        }

        return user;
    }

    @Override
    public Either<Errors, PromoteOutput> process(PromoteInput input) {
        return Try.of(() -> {
                    log.info("Start process input:{}", input);
                    validate(input);
                    AuthenticateOutput authenticateOutput = getAuthentication(input.getJwtHeader());
                    if(!authenticateOutput.getRole().equals(UserRole.ADMIN)) {
                        throw new InvalidAccessException(UserRole.ADMIN);
                    }

                    User user = getUserToPromote(input.getUserId());
                    user.setUserRole(com.tinqinacademy.authentication.persistence.model.enums.UserRole.ADMIN);
                    userRepository.save(user);

                    PromoteOutput output = PromoteOutput.builder()
                                    .build();

                    log.info("End process result:{}", output);

                    return output;
                })
                .toEither()
                .mapLeft(errorMapper::map);
    }
}
