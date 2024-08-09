package com.tinqinacademy.authentication.api;

public class RestApiRoutes {
    public static final String API = "/api/v1";

    public static final String AUTH_BASE =  API+"/auth";

    public static final String AUTH_AUTHENTICATE = AUTH_BASE+"/authenticate";
    public static final String AUTH_REGISTER = AUTH_BASE+"/register";
    public static final String AUTH_LOGIN = AUTH_BASE+"/login";
    public static final String AUTH_PROMOTE = AUTH_BASE+"/promote";
    public static final String AUTH_DEMOTE = AUTH_BASE+"/demote";
    public static final String AUTH_CHANGE_PASSWORD = AUTH_BASE+"/change-password";
}
