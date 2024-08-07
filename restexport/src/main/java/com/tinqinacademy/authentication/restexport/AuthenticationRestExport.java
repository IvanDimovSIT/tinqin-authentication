package com.tinqinacademy.authentication.restexport;



import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateInput;
import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOutput;
import feign.Headers;
import feign.RequestLine;
import org.springframework.web.bind.annotation.RequestBody;


@Headers({"Content-Type: application/json"})
public interface AuthenticationRestExport {

    @RequestLine("POST /api/v1/auth/authenticate")
    AuthenticateOutput authenticate(@RequestBody AuthenticateInput input);

}
