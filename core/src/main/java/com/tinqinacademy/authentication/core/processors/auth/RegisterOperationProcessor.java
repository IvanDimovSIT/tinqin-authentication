package com.tinqinacademy.authentication.core.processors.auth;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.operations.register.RegisterInput;
import com.tinqinacademy.authentication.api.operations.register.RegisterOperation;
import com.tinqinacademy.authentication.api.operations.register.RegisterOutput;
import com.tinqinacademy.authentication.core.email.AuthenticationMailSender;
import com.tinqinacademy.authentication.core.errors.ErrorMapper;
import com.tinqinacademy.authentication.core.processors.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.security.HashingUtil;
import com.tinqinacademy.authentication.core.util.RandomCodeGenerator;
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
public class RegisterOperationProcessor extends BaseOperationProcessor implements RegisterOperation {
    private final UserRepository userRepository;
    private final AuthenticationMailSender authenticationMailSender;
    private final EmailActivationCodeRepository emailActivationCodeRepository;
    private final HashingUtil hashingUtil;
    private final RandomCodeGenerator randomCodeGenerator;

    public RegisterOperationProcessor(ConversionService conversionService, ErrorMapper errorMapper,
                                      Validator validator, UserRepository userRepository,
                                      AuthenticationMailSender authenticationMailSender,
                                      EmailActivationCodeRepository emailActivationCodeRepository,
                                      HashingUtil hashingUtil, RandomCodeGenerator randomCodeGenerator) {
        super(conversionService, errorMapper, validator);
        this.userRepository = userRepository;
        this.authenticationMailSender = authenticationMailSender;
        this.emailActivationCodeRepository = emailActivationCodeRepository;
        this.hashingUtil = hashingUtil;
        this.randomCodeGenerator = randomCodeGenerator;
    }

    private void sendEmailCode(String email, String code) {
        authenticationMailSender.send(email, "Email Activation Code",
                String.format("The hotel system registration code is: \"%s\"", code));
    }

    private String saveCodeForEmail(String email) {
        EmailActivationCode emailActivationCode = EmailActivationCode.builder()
                .activationCode(randomCodeGenerator.generate(6))
                .email(email)
                .build();

        return emailActivationCodeRepository.save(emailActivationCode).getActivationCode();
    }

    @Override
    public Either<Errors, RegisterOutput> process(RegisterInput input) {
        return Try.of(() -> {
                    log.info("Start process input:{}", input);
                    validate(input);

                    User user = conversionService.convert(input, User.class);
                    user.setPassword(hashingUtil.hashPassword(input.getPassword()));
                    User savedUser = userRepository.save(user);
                    String code = saveCodeForEmail(user.getEmail());
                    sendEmailCode(savedUser.getEmail(), code);

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
