package org.example.bankcards.service;

import org.example.bankcards.dto.user.request.RefreshTokenRequestDTO;
import org.example.bankcards.dto.user.request.UserAuthRequestDTO;
import org.example.bankcards.dto.user.request.UserRegisterRequestDTO;
import org.example.bankcards.dto.user.response.UserAuthResponseDTO;
import org.example.bankcards.entity.RefreshTokenEntity;
import org.example.bankcards.entity.UserEntity;
import org.example.bankcards.exception.ConflictException;
import org.example.bankcards.repository.UserRepository;
import org.example.bankcards.security.UserDetailsServiceImpl;
import org.example.bankcards.service.impl.AuthServiceImpl;
import org.example.bankcards.util.JwtUtil;
import org.example.bankcards.util.TestObjectUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthServiceImpl authService;

    private final UserRegisterRequestDTO userRegisterRequestDTO = TestObjectUtils.createRegisterRequest();
    private final UserEntity userEntity = TestObjectUtils.createUserEntity();
    private final UserAuthRequestDTO userAuthRequestDTO = TestObjectUtils.createLoginRequest();
    private final RefreshTokenEntity refreshToken = TestObjectUtils.createRefreshToken(userEntity);


    @Test
    void shouldNotShouldRegisterUserWhenPhoneAlreadyExistsTest() {
        // given

        // when
        when(userRepository.existsByPhone(userRegisterRequestDTO.getPhone())).thenReturn(true);

        // then
        assertThrows(ConflictException.class, () -> authService.registerUser(userRegisterRequestDTO));
        verify(userRepository, never()).save(any());
    }


    @Test
    void shouldLoginUserTest() {
        // given
        UserDetails userDetails = mock(UserDetails.class);


        // when
        when(userService.getUserEntityByPhone(userAuthRequestDTO.getPhone())).thenReturn(userEntity);
        when(userDetailsService.loadUserByUsername(userAuthRequestDTO.getPhone())).thenReturn(userDetails);
        when(jwtUtil.generateAccessToken(userDetails)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(userEntity)).thenReturn(refreshToken);
        UserAuthResponseDTO response = authService.login(userAuthRequestDTO);

        // then
        assertNotNull(response);
        assertEquals("access-token", response.getAccessToken());
        assertEquals(refreshToken.getToken(), response.getRefreshToken());
    }

    @Test
    void shouldNotLoginUserWhenAuthenticationFailsTest() {
        // given


        // when
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));


        // then
        assertThrows(BadCredentialsException.class, () -> authService.login(userAuthRequestDTO));
        verify(userService, never()).getUserEntityByPhone(anyString());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtil, never()).generateAccessToken(any());
        verify(refreshTokenService, never()).createRefreshToken(any());
    }


    @Test
    void shouldRefreshTokenTest() {
        // given
        String refreshTokenValue = refreshToken.getToken();
        RefreshTokenRequestDTO requestDTO = new RefreshTokenRequestDTO(refreshTokenValue);
        UserEntity user = refreshToken.getUser();
        UserDetails userDetails = mock(UserDetails.class);

        // when
        when(refreshTokenService.getRefreshToken(refreshTokenValue)).thenReturn(refreshToken);
        when(userDetailsService.loadUserByUsername(user.getPhone())).thenReturn(userDetails);
        when(jwtUtil.generateAccessToken(userDetails)).thenReturn("new-access-token");

        // then
        UserAuthResponseDTO response = authService.refreshToken(requestDTO);

        assertNotNull(response);
        assertEquals("new-access-token", response.getAccessToken());
        assertEquals(refreshTokenValue, response.getRefreshToken());
    }
}

