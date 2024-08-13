package com.tinqinacademy.authentication.persistence.repository;

import com.tinqinacademy.authentication.persistence.model.RecoverPasswordCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RecoverPasswordCodeRepository extends JpaRepository<RecoverPasswordCode, UUID> {
    Optional<RecoverPasswordCode> findByRecoveryCode(String code);
    Optional<RecoverPasswordCode> findByEmail(String email);
}
