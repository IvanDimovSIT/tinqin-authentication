package com.tinqinacademy.authentication.core.processors.auth;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.exception.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.exception.exceptions.RestPasswordException;
import com.tinqinacademy.authentication.api.operations.resetpassword.ResetPasswordInput;
import com.tinqinacademy.authentication.api.operations.resetpassword.ResetPasswordOperation;
import com.tinqinacademy.authentication.api.operations.resetpassword.ResetPasswordOutput;
import com.tinqinacademy.authentication.core.errors.ErrorMapper;
import com.tinqinacademy.authentication.core.processors.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.security.HashingUtil;
import com.tinqinacademy.authentication.persistence.model.RecoverPasswordCode;
import com.tinqinacademy.authentication.persistence.model.User;
import com.tinqinacademy.authentication.persistence.repository.RecoverPasswordCodeRepository;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ResetPasswordOperationProcessor extends BaseOperationProcessor implements ResetPasswordOperation {
    private final RecoverPasswordCodeRepository recoverPasswordCodeRepository;
    private final UserRepository userRepository;
    private final HashingUtil hashingUtil;

    @Value("${password-recovery-code.validity-minutes}")
    private int codeValidityMinutes;

    public ResetPasswordOperationProcessor(ConversionService conversionService, ErrorMapper errorMapper,
                                           Validator validator,
                                           RecoverPasswordCodeRepository recoverPasswordCodeRepository,
                                           UserRepository userRepository, HashingUtil hashingUtil) {
        super(conversionService, errorMapper, validator);
        this.recoverPasswordCodeRepository = recoverPasswordCodeRepository;
        this.userRepository = userRepository;
        this.hashingUtil = hashingUtil;
    }

    private RecoverPasswordCode getRecoverPasswordCode(String code) {
        RecoverPasswordCode recoverPasswordCode = recoverPasswordCodeRepository.findByRecoveryCode(code).orElseThrow(
                () -> new NotFoundException(String.format("Password recover code:%s", code)));

        LocalDateTime validity = recoverPasswordCode.getCreated().plusMinutes(codeValidityMinutes);
        if(validity.isBefore(LocalDateTime.now())) {
            throw new RestPasswordException(String.format("Password recovery code is expired, expires at:%s",
                    validity.toString()));
        }

        return recoverPasswordCode;
    }

    private User getUser(String email){
        return userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(String.format("User with email:%s", email)));
    }

    @Override
    public Either<Errors, ResetPasswordOutput> process(ResetPasswordInput input) {
        return Try.of(() -> {
                    log.info("Start process input:{}", input);
                    validate(input);

                    RecoverPasswordCode recoverPasswordCode = getRecoverPasswordCode(input.getRecoveryCode());
                    User user = getUser(recoverPasswordCode.getEmail());
                    String newPasswordHash = hashingUtil.hashPassword(input.getNewPassword());
                    user.setPassword(newPasswordHash);

                    userRepository.save(user);
                    recoverPasswordCodeRepository.delete(recoverPasswordCode);

                    ResetPasswordOutput output = ResetPasswordOutput.builder()
                            .build();
                    log.info("End process result:{}", output);

                    return output;
                })
                .toEither()
                .mapLeft(errorMapper::map);
    }
}
