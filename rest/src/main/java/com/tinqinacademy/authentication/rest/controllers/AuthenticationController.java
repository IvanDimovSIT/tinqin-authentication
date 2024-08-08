package com.tinqinacademy.authentication.rest.controllers;

import com.tinqinacademy.authentication.api.RestApiRoutes;
import com.tinqinacademy.authentication.api.errors.Errors;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateInput;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOperation;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOutput;
import com.tinqinacademy.authentication.api.operations.login.LoginInput;
import com.tinqinacademy.authentication.api.operations.login.LoginOperation;
import com.tinqinacademy.authentication.api.operations.login.LoginOutput;
import com.tinqinacademy.authentication.api.operations.register.RegisterInput;
import com.tinqinacademy.authentication.api.operations.register.RegisterOperation;
import com.tinqinacademy.authentication.api.operations.register.RegisterOutput;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.vavr.control.Either;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.header.Header;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthenticationController extends BaseController {
    private final AuthenticateOperation authenticateOperation;
    private final RegisterOperation registerOperation;
    private final LoginOperation loginOperation;

    @Operation(summary = "Authenticates JWT", description = "Returns user details for JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "403", description = "Forbidden"),
            @ApiResponse(responseCode = "404", description = "NotFound"),
    })
    @PostMapping(RestApiRoutes.AUTH_AUTHENTICATE)
    public ResponseEntity<?> authenticate(@RequestBody AuthenticateInput input) {
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
}
