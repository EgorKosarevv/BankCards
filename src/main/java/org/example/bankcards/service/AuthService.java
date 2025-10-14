package org.example.bankcards.service;

import org.example.bankcards.dto.user.request.RefreshTokenRequestDTO;
import org.example.bankcards.dto.user.request.UserAuthRequestDTO;
import org.example.bankcards.dto.user.request.UserRegisterRequestDTO;
import org.example.bankcards.dto.user.response.UserAuthResponseDTO;

public interface AuthService {
    void registerUser(UserRegisterRequestDTO request);
    UserAuthResponseDTO login(UserAuthRequestDTO request);
    UserAuthResponseDTO refreshToken(RefreshTokenRequestDTO request);
}
