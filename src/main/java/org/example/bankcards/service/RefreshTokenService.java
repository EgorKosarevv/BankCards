package org.example.bankcards.service;

import org.example.bankcards.entity.RefreshTokenEntity;
import org.example.bankcards.entity.UserEntity;


public interface RefreshTokenService {
    RefreshTokenEntity createRefreshToken(UserEntity user);
    RefreshTokenEntity getRefreshToken(String token);
    void cleanupExpiredTokens();
}
