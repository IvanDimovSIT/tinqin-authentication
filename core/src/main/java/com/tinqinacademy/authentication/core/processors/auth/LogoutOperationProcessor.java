package com.tinqinacademy.authentication.core.processors.auth;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.exception.exceptions.InvalidTokenException;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateInput;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOperation;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOutput;
import com.tinqinacademy.authentication.api.operations.logout.LogoutInput;
import com.tinqinacademy.authentication.api.operations.logout.LogoutOperation;
import com.tinqinacademy.authentication.api.operations.logout.LogoutOutput;
import com.tinqinacademy.authentication.core.errors.ErrorMapper;
import com.tinqinacademy.authentication.core.processors.BaseOperationProcessor;
import com.tinqinacademy.authentication.persistence.model.BlacklistedToken;
import com.tinqinacademy.authentication.persistence.repository.BlacklistedTokenRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LogoutOperationProcessor extends BaseOperationProcessor implements LogoutOperation {
    private final AuthenticateOperation authenticateOperation;
    private final BlacklistedTokenRepository blacklistedTokenRepository;
    public LogoutOperationProcessor(ConversionService conversionService, ErrorMapper errorMapper,
                                    Validator validator, AuthenticateOperation authenticateOperation,
                                    BlacklistedTokenRepository blacklistedTokenRepository) {
        super(conversionService, errorMapper, validator);
        this.authenticateOperation = authenticateOperation;
        this.blacklistedTokenRepository = blacklistedTokenRepository;
    }

    void validateHeader(String jwtHeader) {
        Either<Errors, AuthenticateOutput> output =
                authenticateOperation.process(AuthenticateInput.builder()
                        .jwtHeader(jwtHeader)
                        .build());

        if (output.isLeft()) {
            throw new InvalidTokenException("Invalid token, can't logout");
        }
    }

    private void blacklistToken(LogoutInput input) {
        BlacklistedToken token = BlacklistedToken.builder()
                .token(input.getJwtHeader().substring(7))
                .build();

        blacklistedTokenRepository.save(token);
    }

    @Override
    public Either<Errors, LogoutOutput> process(LogoutInput input) {
        return Try.of(() -> {
                    log.info("Start process input:{}", input);
                    validate(input);
                    validateHeader(input.getJwtHeader());
                    blacklistToken(input);

                    LogoutOutput output = LogoutOutput.builder()
                            .build();

                    log.info("End process result:{}", output);

                    return output;
                })
                .toEither()
                .mapLeft(errorMapper::map);
    }
}
