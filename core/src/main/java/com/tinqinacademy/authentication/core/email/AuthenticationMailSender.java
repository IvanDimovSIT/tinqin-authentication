package com.tinqinacademy.authentication.core.email;

public interface AuthenticationMailSender {
    void send(String to, String subject, String contents);
}
