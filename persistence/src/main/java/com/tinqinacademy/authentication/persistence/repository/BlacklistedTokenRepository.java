package com.tinqinacademy.authentication.persistence.repository;

import com.tinqinacademy.authentication.persistence.model.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, UUID> {

    @Query("SELECT b FROM BlacklistedToken b WHERE b.created + 5 MINUTE < CURRENT_DATE")
    List<BlacklistedToken> getOldTokens();

    boolean existsByToken(String token);
}
