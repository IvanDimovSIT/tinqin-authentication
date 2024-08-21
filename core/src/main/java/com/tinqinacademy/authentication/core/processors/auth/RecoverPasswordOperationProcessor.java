package com.tinqinacademy.authentication.core.processors.auth;

import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordInput;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordOperation;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordOutput;
import com.tinqinacademy.authentication.core.email.AuthenticationMailSender;
import com.tinqinacademy.authentication.core.errors.ErrorMapper;
import com.tinqinacademy.authentication.core.processors.BaseOperationProcessor;
import com.tinqinacademy.authentication.core.util.RandomCodeGenerator;
import com.tinqinacademy.authentication.persistence.model.RecoverPasswordCode;
import com.tinqinacademy.authentication.persistence.repository.RecoverPasswordCodeRepository;
import com.tinqinacademy.authentication.persistence.repository.UserRepository;
import io.vavr.control.Either;
import io.vavr.control.Try;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
public class RecoverPasswordOperationProcessor extends BaseOperationProcessor implements RecoverPasswordOperation {
    private static final int NUMBER_OF_TRIES_FOR_GENERATED_CODE = 3;
    private static final int CODE_LENGTH = 10;

    private final UserRepository userRepository;
    private final RecoverPasswordCodeRepository recoverPasswordCodeRepository;
    private final AuthenticationMailSender authenticationMailSender;
    private final RandomCodeGenerator randomCodeGenerator;


    public RecoverPasswordOperationProcessor(ConversionService conversionService, ErrorMapper errorMapper,
                                             Validator validator, UserRepository userRepository,
                                             RecoverPasswordCodeRepository recoverPasswordCodeRepository,
                                             AuthenticationMailSender authenticationMailSender,
                                             RandomCodeGenerator randomCodeGenerator) {
        super(conversionService, errorMapper, validator);
        this.userRepository = userRepository;
        this.recoverPasswordCodeRepository = recoverPasswordCodeRepository;
        this.authenticationMailSender = authenticationMailSender;
        this.randomCodeGenerator = randomCodeGenerator;
    }

    private boolean checkUserWithEmailExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private String generateRandomCode() {
        String randomCode;
        for(int i=0; i<NUMBER_OF_TRIES_FOR_GENERATED_CODE; i++){
            randomCode = randomCodeGenerator.generate(CODE_LENGTH);
            if(recoverPasswordCodeRepository.findByRecoveryCode(randomCode).isEmpty()){
                return randomCode;
            }
        }
        throw new RuntimeException("Could not generate random code");
    }

    private RecoverPasswordCode createCodeForEmail(String email) {
        Optional<RecoverPasswordCode> existingCode = recoverPasswordCodeRepository.findByEmail(email);
        existingCode.ifPresent(recoverPasswordCodeRepository::delete);

        String randomCode = generateRandomCode();

        RecoverPasswordCode recoverPasswordCode = RecoverPasswordCode.builder()
                .email(email)
                .recoveryCode(randomCode)
                .build();

        return recoverPasswordCodeRepository.save(recoverPasswordCode);
    }

    private void sendEmail(RecoverPasswordCode recoverPasswordCode) {
        authenticationMailSender.send(recoverPasswordCode.getEmail(), "Recover hotel account code",
                String.format("The hotel system password recovery code is: \"%s\"",
                        recoverPasswordCode.getRecoveryCode()));
    }

    @Override
    public Either<Errors, RecoverPasswordOutput> process(RecoverPasswordInput input) {
        return Try.of(() -> {
                    log.info("Start process input:{}", input);
                    validate(input);

                    RecoverPasswordOutput output = RecoverPasswordOutput.builder()
                            .build();

                    if (!checkUserWithEmailExists(input.getEmail())) {
                        log.info("End process result:{}", output);
                        return output;
                    }
                    RecoverPasswordCode code = createCodeForEmail(input.getEmail());
                    sendEmail(code);

                    log.info("End process result:{}", output);

                    return output;
                })
                .toEither()
                .mapLeft(errorMapper::map);
    }
}
