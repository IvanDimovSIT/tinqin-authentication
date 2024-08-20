package com.tinqinacademy.authentication.core.email;

import org.springframework.scheduling.annotation.Async;

public interface AuthenticationMailSender {
    @Async
    void send(String to, String subject, String contents);
}
