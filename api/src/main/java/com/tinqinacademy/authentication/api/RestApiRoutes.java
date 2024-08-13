package com.tinqinacademy.authentication.api;

public class RestApiRoutes {
    public static final String API = "/api/v1";

    public static final String AUTH_BASE =  API+"/auth";

    public static final String AUTH_AUTHENTICATE = AUTH_BASE+"/authenticate";
    public static final String AUTH_REGISTER = AUTH_BASE+"/register";
    public static final String AUTH_LOGIN = AUTH_BASE+"/login";
    public static final String AUTH_LOGOUT = AUTH_BASE+"/logout";
    public static final String AUTH_PROMOTE = AUTH_BASE+"/promote";
    public static final String AUTH_DEMOTE = AUTH_BASE+"/demote";
    public static final String AUTH_CHANGE_PASSWORD = AUTH_BASE+"/change-password";
    public static final String AUTH_CONFIRM_REGISTRATION = AUTH_BASE+"/confirm-registration";
    public static final String AUTH_RECOVER_PASSWORD = AUTH_BASE+"/recover-password";
    public static final String AUTH_RESET_PASSWORD = AUTH_BASE+"/reset-password";
}
