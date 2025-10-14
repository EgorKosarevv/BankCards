package org.example.bankcards.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bankcards.entity.RefreshTokenEntity;
import org.example.bankcards.entity.UserEntity;
import org.example.bankcards.exception.NotFoundException;
import org.example.bankcards.repository.RefreshTokenRepository;
import org.example.bankcards.service.RefreshTokenService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${jwt.refreshExpirationMinutes}")
    private long refreshExpirationMinutes;

    @Override
    public RefreshTokenEntity createRefreshToken(UserEntity user) {
        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setToken(UUID.randomUUID().toString());
        token.setUser(user);
        token.setExpiryDate(Instant.now().plus(refreshExpirationMinutes, ChronoUnit.MINUTES));
        refreshTokenRepository.save(token);
        log.info("The refresh token has been created: {} for user: {}", token.getToken(), user.getPhone());
        return token;

    }

    public RefreshTokenEntity getRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException("Invalid refresh token"));
    }


    @Scheduled(fixedRate = 24 * 60 * 60 * 1000)
    @Transactional
    @Override
    public void cleanupExpiredTokens() {
        Instant now = Instant.now();
        int deletedCount = refreshTokenRepository.deleteByExpiryDateBefore(now);
        log.info("Expired tokens removed: {}", deletedCount);
    }
}
