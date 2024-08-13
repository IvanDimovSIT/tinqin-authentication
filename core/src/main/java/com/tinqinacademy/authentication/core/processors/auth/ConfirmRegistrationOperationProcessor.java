package com.tinqinacademy.authentication.core.processors.auth;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.exception.exceptions.NotFoundException;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordOutput;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationInput;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationOperation;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationOutput;
import com.tinqinacademy.authentication.core.errors.ErrorMapper;
import com.tinqinacademy.authentication.core.processors.BaseOperationProcessor;
import com.tinqinacademy.authentication.persistence.model.EmailActivationCode;
import com.tinqinacademy.authentication.persistence.model.User;
import com.tinqinacademy.authentication.persistence.repository.EmailActivationCodeRepository;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConfirmRegistrationOperationProcessor extends BaseOperationProcessor implements ConfirmRegistrationOperation {
    private final UserRepository userRepository;
    private final EmailActivationCodeRepository emailActivationCodeRepository;

    public ConfirmRegistrationOperationProcessor(ConversionService conversionService, ErrorMapper errorMapper,
                                                 Validator validator, UserRepository userRepository,
                                                 EmailActivationCodeRepository emailActivationCodeRepository) {
        super(conversionService, errorMapper, validator);
        this.userRepository = userRepository;
        this.emailActivationCodeRepository = emailActivationCodeRepository;
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new NotFoundException(String.format("User with email:%s", email)));
    }

    private EmailActivationCode getCode(String code){
        return emailActivationCodeRepository.findByActivationCode(code)
                .orElseThrow(() -> new NotFoundException(String.format("Activation code:%s", code)));
    }

    @Override
    public Either<Errors, ConfirmRegistrationOutput> process(ConfirmRegistrationInput input) {
        return Try.of(() -> {
                    log.info("Start process input:{}", input);
                    validate(input);

                    EmailActivationCode emailActivationCode = getCode(input.getConfirmationCode());
                    User user = getUser(emailActivationCode.getEmail());
                    user.setIsActivated(true);
                    emailActivationCodeRepository.delete(emailActivationCode);
                    userRepository.save(user);

                    ConfirmRegistrationOutput output = ConfirmRegistrationOutput.builder()
                            .build();

                    log.info("End process result:{}", output);

                    return output;
                })
                .toEither()
                .mapLeft(errorMapper::map);
    }
}
