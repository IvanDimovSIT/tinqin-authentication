package com.tinqinacademy.authentication.core.scheduled;

import com.tinqinacademy.authentication.persistence.model.BlacklistedToken;
import com.tinqinacademy.authentication.persistence.repository.BlacklistedTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BlacklistedTokensRemover {
    private final BlacklistedTokenRepository blacklistedTokenRepository;

    @Scheduled(cron = "0 0 3 1/1 * ?")
    public void clearBlacklistedTokens(){
        List<BlacklistedToken> tokens = blacklistedTokenRepository.getOldTokens();
        blacklistedTokenRepository.deleteAll(tokens);
    }

}
