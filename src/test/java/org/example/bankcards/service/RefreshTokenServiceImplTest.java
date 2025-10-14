package org.example.bankcards.service;

import org.example.bankcards.entity.RefreshTokenEntity;
import org.example.bankcards.exception.NotFoundException;
import org.example.bankcards.repository.RefreshTokenRepository;
import org.example.bankcards.service.impl.RefreshTokenServiceImpl;
import org.example.bankcards.util.TestObjectUtils;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceImplTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    private final RefreshTokenEntity refreshTokenEntity = TestObjectUtils.createRefreshTokenEntity();


    @Test
    void shouldGetRefreshTokenTest() {
        // given
        String tokenStr = "valid-token";

        // when
        when(refreshTokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(refreshTokenEntity));
        RefreshTokenEntity result = refreshTokenService.getRefreshToken(tokenStr);

        // then
        assertEquals(refreshTokenEntity, result);
    }

    @Test
    void shouldNotGetRefreshTokenWhenNotFoundTest() {
        // given
        String tokenStr = "missing-token";
        String expected = "Invalid refresh token";

        // when
        when(refreshTokenRepository.findByToken(tokenStr)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () ->
                refreshTokenService.getRefreshToken(tokenStr));

        // then
        assertTrue(exception.getMessage().contains(expected));
    }

    @Test
    void shouldCleanupExpiredTokensTest() {
        // given
        when(refreshTokenRepository.deleteByExpiryDateBefore(any())).thenReturn(3);

        // when
        refreshTokenService.cleanupExpiredTokens();

        // then
        verify(refreshTokenRepository).deleteByExpiryDateBefore(any());
    }

    @Test
    void shouldNotCleanupExpiredTokensWhenHibernateExceptionTest() {
        // given

        // when
        when(refreshTokenRepository.deleteByExpiryDateBefore(any()))
                .thenThrow(new HibernateException("DB error"));

        // then
        assertThrows(HibernateException.class, () ->
                refreshTokenService.cleanupExpiredTokens()
        );
    }
}
