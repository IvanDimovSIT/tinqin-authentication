package com.tinqinacademy.authentication.core.processors.auth;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.exception.exceptions.*;
import com.tinqinacademy.authentication.api.model.enums.UserRole;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateInput;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOperation;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOutput;
import com.tinqinacademy.authentication.api.operations.demote.DemoteInput;
import com.tinqinacademy.authentication.api.operations.demote.DemoteOperation;
import com.tinqinacademy.authentication.api.operations.demote.DemoteOutput;
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

import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class DemoteOperationProcessor extends BaseOperationProcessor implements DemoteOperation {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final AuthenticateOperation authenticateOperation;

    public DemoteOperationProcessor(ConversionService conversionService, ErrorMapper errorMapper,
                                    Validator validator, JwtUtil jwtUtil, UserRepository userRepository,
                                    AuthenticateOperation authenticateOperation) {
        super(conversionService, errorMapper, validator);
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.authenticateOperation = authenticateOperation;
    }

    private UserToken getAuthentication(String header) {
        AuthenticateInput authenticateInput = AuthenticateInput.builder()
                .jwtHeader(header)
                .build();

        Either<Errors, AuthenticateOutput> output = authenticateOperation.process(authenticateInput);
        if(output.isLeft()) {
            throw new InvalidTokenException("Invalid token");
        }

        return jwtUtil.extractFromHeader(header);
    }

    private User getUserToDemote(String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(
                () -> new NotFoundException(String.format("User with id:%s", userId)));

        if(!user.getUserRole().toString().equals(UserRole.ADMIN.toString())) {
            throw new DemoteException("User cannot be demoted, is not an admin");
        }

        return user;
    }

    @Override
    public Either<Errors, DemoteOutput> process(DemoteInput input) {
        return Try.of(() -> {
                    log.info("Start process input:{}", input);
                    validate(input);
                    UserToken userToken = getAuthentication(input.getJwtHeader());
                    if(!userToken.getRole().equals(UserRole.ADMIN.toString())) {
                        throw new InvalidAccessException(UserRole.ADMIN);
                    }

                    User user = getUserToDemote(input.getUserId());
                    if(Objects.equals(user.getId().toString(), userToken.getId())){
                        throw new DemoteException("Admin cannot demote self");
                    }

                    user.setUserRole(com.tinqinacademy.authentication.persistence.model.enums.UserRole.USER);
                    userRepository.save(user);

                    DemoteOutput output = DemoteOutput.builder()
                            .build();

                    log.info("End process result:{}", output);

                    return output;
                })
                .toEither()
                .mapLeft(errorMapper::map);
    }
}
