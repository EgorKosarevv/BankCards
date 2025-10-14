package org.example.bankcards.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bankcards.dto.user.request.RefreshTokenRequestDTO;
import org.example.bankcards.dto.user.request.UserAuthRequestDTO;
import org.example.bankcards.dto.user.request.UserRegisterRequestDTO;
import org.example.bankcards.dto.user.response.UserAuthResponseDTO;
import org.example.bankcards.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Operations related to user authentication and registration")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Authenticate a user and return access and refresh tokens.")
    @PostMapping("/login")
    public ResponseEntity<UserAuthResponseDTO> login(@RequestBody @Valid UserAuthRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }


    @Operation(summary = "Register", description = "Register a new user in the system.")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid UserRegisterRequestDTO request) {
        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @Operation(summary = "Refresh Token", description = "Update the access token using a refresh token.")
    @PostMapping("/updateToken")
    public ResponseEntity<UserAuthResponseDTO> refreshToken(@RequestBody @Valid RefreshTokenRequestDTO request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }

}