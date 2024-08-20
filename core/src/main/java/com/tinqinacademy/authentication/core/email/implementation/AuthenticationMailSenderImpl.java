package com.tinqinacademy.authentication.core.email.implementation;

import com.tinqinacademy.authentication.core.email.AuthenticationMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationMailSenderImpl implements AuthenticationMailSender {
    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender emailSender;

    @Override
    @Async
    @Retryable(
            value = { Exception.class },
            maxAttempts = 5,
            backoff = @Backoff(delay = 2000)
    )
    public void send(String to, String subject, String contents) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(contents);
        emailSender.send(message);
        log.info("Email sent to {} from {}", to, from);
    }

    @Recover
    public void recover(Exception exception, String to, String subject, String contents) {
        log.error("Error:{} Email failed to send to {} from {}", exception.getMessage(), to, from);
    }
}
