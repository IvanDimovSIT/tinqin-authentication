package com.tinqinacademy.authentication.core.email.implementation;

import com.tinqinacademy.authentication.core.email.AuthenticationMailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationMailSenderImpl implements AuthenticationMailSender {
    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender emailSender;

    @Override
    public void send(String to, String subject, String contents) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(contents);
        emailSender.send(message);

        log.info("Email sent to {}", to);
    }
}
