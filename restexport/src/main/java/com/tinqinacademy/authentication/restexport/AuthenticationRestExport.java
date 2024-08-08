package com.tinqinacademy.authentication.restexport;



import com.tinqinacademy.authentication.api.operations.authenticate.AuthenticateOutput;
import feign.Headers;
import feign.Param;
import feign.RequestLine;


@Headers({"Content-Type: application/json"})
public interface AuthenticationRestExport {

    @RequestLine("POST /api/v1/auth/authenticate")
    @Headers({"Authorization: {jwtHeader}"})
    AuthenticateOutput authenticate(@Param("jwtHeader") String jwtHeader);

}
