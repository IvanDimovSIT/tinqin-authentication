package com.tinqinacademy.authentication.persistence.model;

import com.tinqinacademy.authentication.persistence.model.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "username", nullable = false, length = 64)
    private String username;

    @Column(name = "email", nullable = false, length = 64)
    private String email;

    @Column(name = "password", nullable = false, length = 256)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 32)
    private UserRole userRole;

    @Column(name = "first_name", nullable = false, length = 64)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 64)
    private String lastName;

    @Column(name = "phone_number", nullable = false, length = 16)
    private String phoneNumber;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @CreationTimestamp
    @Column(name = "created")
    private LocalDateTime created;

    @UpdateTimestamp
    @Column(name = "last_modified")
    private LocalDateTime lastModified;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(userRole);
    }
}
