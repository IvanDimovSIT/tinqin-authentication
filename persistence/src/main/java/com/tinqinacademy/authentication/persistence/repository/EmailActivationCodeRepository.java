package com.tinqinacademy.authentication.persistence.repository;

import com.tinqinacademy.authentication.persistence.model.EmailActivationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmailActivationCodeRepository extends JpaRepository<EmailActivationCode, UUID> {
    Optional<EmailActivationCode> findByActivationCode(String code);
}
