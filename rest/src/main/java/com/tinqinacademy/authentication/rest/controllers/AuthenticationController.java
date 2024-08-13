package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.RestApiRoutes;
import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateInput;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOperation;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOutput;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordInput;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordOperation;
import com.tinqinacademy.authentication.api.operations.changepassword.ChangePasswordOutput;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationInput;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationOperation;
import com.tinqinacademy.authentication.api.operations.confirmregistration.ConfirmRegistrationOutput;
import com.tinqinacademy.authentication.api.operations.demote.DemoteInput;
import com.tinqinacademy.authentication.api.operations.demote.DemoteOperation;
import com.tinqinacademy.authentication.api.operations.demote.DemoteOutput;
import com.tinqinacademy.authentication.api.operations.login.LoginInput;
import com.tinqinacademy.authentication.api.operations.login.LoginOperation;
import com.tinqinacademy.authentication.api.operations.login.LoginOutput;
import com.tinqinacademy.authentication.api.operations.logout.LogoutInput;
import com.tinqinacademy.authentication.api.operations.logout.LogoutOperation;
import com.tinqinacademy.authentication.api.operations.logout.LogoutOutput;
import com.tinqinacademy.authentication.api.operations.promote.PromoteInput;
import com.tinqinacademy.authentication.api.operations.promote.PromoteOperation;
import com.tinqinacademy.authentication.api.operations.promote.PromoteOutput;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordInput;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordOperation;
import com.tinqinacademy.authentication.api.operations.recoverpassword.RecoverPasswordOutput;
import com.tinqinacademy.authentication.api.operations.register.RegisterInput;
import com.tinqinacademy.authentication.api.operations.register.RegisterOperation;
import com.tinqinacademy.authentication.api.operations.register.RegisterOutput;
import com.tinqinacademy.authentication.api.operations.resetpassword.ResetPasswordInput;
import com.tinqinacademy.authentication.api.operations.resetpassword.ResetPasswordOperation;
import com.tinqinacademy.authentication.api.operations.resetpassword.ResetPasswordOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController extends BaseController {
    private final AuthenticateOperation authenticateOperation;
    private final RegisterOperation registerOperation;
    private final LoginOperation loginOperation;
    private final PromoteOperation promoteOperation;
    private final DemoteOperation demoteOperation;
    private final ChangePasswordOperation changePasswordOperation;
    private final LogoutOperation logoutOperation;
    private final ConfirmRegistrationOperation confirmRegistrationOperation;
    private final RecoverPasswordOperation recoverPasswordOperation;
    private final ResetPasswordOperation resetPasswordOperation;

    @Operation(summary = "Authenticates JWT", description = "Returns user details for JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "NotFound"),
    })
    @PostMapping(RestApiRoutes.AUTH_AUTHENTICATE)
    public ResponseEntity<?> authenticate(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtHeader) {
        AuthenticateInput input = AuthenticateInput.builder()
                .jwtHeader(jwtHeader)
                .build();
        Either<Errors, AuthenticateOutput> output = authenticateOperation.process(input);

        return mapToResponseEntity(output, HttpStatus.OK);
    }

    @Operation(summary = "Register new user", description = "Registers a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping(RestApiRoutes.AUTH_REGISTER)
    public ResponseEntity<?> register(@RequestBody RegisterInput input) {
        Either<Errors, RegisterOutput> output = registerOperation.process(input);

        return mapToResponseEntity(output, HttpStatus.OK);
    }

    @Operation(summary = "Login user", description = "User logs in with credentials")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request")
    })
    @PostMapping(RestApiRoutes.AUTH_LOGIN)
    public ResponseEntity<?> login(@RequestBody LoginInput input) {
        Either<Errors, LoginOutput> output = loginOperation.process(input);

        if(output.isLeft()) {
            return new ResponseEntity<>(output.getLeft(), output.getLeft().getStatus());
        }else{
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + output.get().getJwtToken());

            return new ResponseEntity<>(output.get(), headers, HttpStatus.OK);
        }
    }

    @Operation(summary = "Promote user", description = "Promotes a user to admin")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "NotFound"),
    })
    @PostMapping(RestApiRoutes.AUTH_PROMOTE)
    public ResponseEntity<?> promote(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtHeader, @RequestBody PromoteInput input) {
        input.setJwtHeader(jwtHeader);

        Either<Errors, PromoteOutput> output = promoteOperation.process(input);

        return mapToResponseEntity(output, HttpStatus.OK);
    }

    @Operation(summary = "Demotes admin", description = "Demotes an admin to user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "NotFound"),
    })
    @PostMapping(RestApiRoutes.AUTH_DEMOTE)
    public ResponseEntity<?> demote(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtHeader, @RequestBody DemoteInput input) {
        input.setJwtHeader(jwtHeader);

        Either<Errors, DemoteOutput> output = demoteOperation.process(input);

        return mapToResponseEntity(output, HttpStatus.OK);
    }

    @Operation(summary = "Change password", description = "Changes the users password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "NotFound"),
    })
    @PostMapping(RestApiRoutes.AUTH_CHANGE_PASSWORD)
    public ResponseEntity<?> changePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtHeader,
                                            @RequestBody ChangePasswordInput input) {
        input.setJwtHeader(jwtHeader);

        Either<Errors, ChangePasswordOutput> output = changePasswordOperation.process(input);

        return mapToResponseEntity(output, HttpStatus.OK);
    }

    @Operation(summary = "Logout user", description = "Logs out the registered user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping(RestApiRoutes.AUTH_LOGOUT)
    public ResponseEntity<?> logout(@RequestHeader(HttpHeaders.AUTHORIZATION) String jwtHeader) {
        LogoutInput input = LogoutInput.builder()
                .jwtHeader(jwtHeader)
                .build();
        Either<Errors, LogoutOutput> output = logoutOperation.process(input);

        return mapToResponseEntity(output, HttpStatus.OK);
    }

    @Operation(summary = "Activate user", description = "Activates user account and allows for login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "NotFound"),
    })
    @PostMapping(RestApiRoutes.AUTH_CONFIRM_REGISTRATION)
    public ResponseEntity<?> confirmRegistration(@RequestBody ConfirmRegistrationInput input) {
        Either<Errors, ConfirmRegistrationOutput> output = confirmRegistrationOperation.process(input);

        return mapToResponseEntity(output, HttpStatus.OK);
    }

    @Operation(summary = "Recover password", description = "Sends password recovery code to email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")
    })
    @PostMapping(RestApiRoutes.AUTH_RECOVER_PASSWORD)
    public ResponseEntity<?> recoverPassword(@RequestBody RecoverPasswordInput input) {
        Either<Errors, RecoverPasswordOutput> output = recoverPasswordOperation.process(input);

        return mapToResponseEntity(output, HttpStatus.OK);
    }

    @Operation(summary = "Reset password with code", description = "Resets the user password with the password" +
            " recovery code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Bad request"),
            @ApiResponse(responseCode = "404", description = "NotFound"),
    })
    @PostMapping(RestApiRoutes.AUTH_RESET_PASSWORD)
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordInput input) {
        Either<Errors, ResetPasswordOutput> output = resetPasswordOperation.process(input);

        return mapToResponseEntity(output, HttpStatus.OK);
    }
}
