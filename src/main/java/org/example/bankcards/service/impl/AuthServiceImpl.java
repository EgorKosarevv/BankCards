package org.example.bankcards.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bankcards.dto.user.request.RefreshTokenRequestDTO;
import org.example.bankcards.dto.user.request.UserAuthRequestDTO;
import org.example.bankcards.dto.user.request.UserRegisterRequestDTO;
import org.example.bankcards.dto.user.response.UserAuthResponseDTO;
import org.example.bankcards.entity.RefreshTokenEntity;
import org.example.bankcards.entity.RoleEntity;
import org.example.bankcards.entity.UserEntity;
import org.example.bankcards.entity.UserRole;
import org.example.bankcards.exception.ConflictException;
import org.example.bankcards.exception.ServiceException;
import org.example.bankcards.mapper.UserMapper;
import org.example.bankcards.repository.UserRepository;
import org.example.bankcards.util.JwtUtil;
import org.example.bankcards.security.UserDetailsServiceImpl;
import org.example.bankcards.service.AuthService;
import org.example.bankcards.service.RefreshTokenService;
import org.example.bankcards.service.RoleService;
import org.example.bankcards.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    private final RefreshTokenService refreshTokenService;
    private final RoleService roleService;
    private final UserService userService;


    public void registerUser(UserRegisterRequestDTO request) {
        validatePhoneNotTaken(request.getPhone());

        RoleEntity userRole = roleService.getRoleByName(UserRole.USER);

        UserEntity user = userMapper.toEntity(request);
        user.setRoles(Set.of(userRole));
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        userRepository.save(user);
        log.info("The user is registered: {}", user);
    }


    public UserAuthResponseDTO login(UserAuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword()));

        UserEntity userEntity = userService.getUserEntityByPhone(request.getPhone());
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(request.getPhone());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        RefreshTokenEntity refreshToken = refreshTokenService.createRefreshToken(userEntity);

        log.info("The user has been successfully logged in");
        return new UserAuthResponseDTO(newAccessToken, refreshToken.getToken());
    }


    public UserAuthResponseDTO refreshToken(RefreshTokenRequestDTO request) {
        RefreshTokenEntity refreshToken = refreshTokenService.getRefreshToken(request.getRefreshToken());

        validateRefreshToken(refreshToken);

        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(refreshToken.getUser().getPhone());
        String newAccessToken = jwtUtil.generateAccessToken(userDetails);
        log.info("The token has been updated");
        return new UserAuthResponseDTO(newAccessToken, refreshToken.getToken());
    }


    private void validateRefreshToken(RefreshTokenEntity token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            throw new ServiceException("The refresh token has expired, log in again");
        }
    }



    private void validatePhoneNotTaken(String phone) {
        if (userRepository.existsByPhone(phone)) {
            throw new ConflictException("The user with this phone already exists");
        }
    }
}
