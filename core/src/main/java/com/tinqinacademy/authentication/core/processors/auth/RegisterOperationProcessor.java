package com.tinqinacademy.authentication.core.processors.auth;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.model.enums.UserRole;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOutput;
import com.tinqinacademy.authentication.api.operations.register.RegisterInput;
import com.tinqinacademy.authentication.api.operations.register.RegisterOperation;
import com.tinqinacademy.authentication.api.operations.register.RegisterOutput;
import com.tinqinacademy.authentication.core.errors.ErrorMapper;
import com.tinqinacademy.authentication.core.processors.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.security.HashingUtil;
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
public class RegisterOperationProcessor extends BaseOperationProcessor implements RegisterOperation {
    private final UserRepository userRepository;
    private final HashingUtil hashingUtil;

    public RegisterOperationProcessor(ConversionService conversionService, ErrorMapper errorMapper,
                                      Validator validator, UserRepository userRepository, HashingUtil hashingUtil) {
        super(conversionService, errorMapper, validator);
        this.userRepository = userRepository;
        this.hashingUtil = hashingUtil;
    }

    @Override
    public Either<Errors, RegisterOutput> process(RegisterInput input) {
        return Try.of(() -> {
                    log.info("Start process input:{}", input);
                    validate(input);

                    User user = conversionService.convert(input, User.class);
                    user.setPassword(hashingUtil.hashPassword(input.getPassword()));
                    User savedUser = userRepository.save(user);

                    RegisterOutput output = RegisterOutput.builder()
                            .id(savedUser.getId().toString())
                            .build();

                    log.info("End process result:{}", output);

                    return output;
                })
                .toEither()
                .mapLeft(errorMapper::map);
    }
}
