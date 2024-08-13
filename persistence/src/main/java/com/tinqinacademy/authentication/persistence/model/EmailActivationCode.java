package com.tinqinacademy.authentication.persistence.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Entity
@Table(name = "email_activation_code")
public class EmailActivationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "email", nullable = false, length = 64, unique = true)
    private String email;

    @Column(name = "activation_code", nullable = false, length = 16)
    private String activationCode;

    @CreationTimestamp
    @Column(name = "created")
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "last_modified")
    private LocalDateTime lastModified;
}
