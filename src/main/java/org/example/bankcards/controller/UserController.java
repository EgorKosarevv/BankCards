package org.example.bankcards.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.bankcards.dto.card.request.CardFilterRequestDTO;
import org.example.bankcards.dto.card.response.CardSimpleResponseDTO;
import org.example.bankcards.dto.user.response.UserResponseDTO;
import org.example.bankcards.security.UserDetailsImpl;
import org.example.bankcards.service.CardService;
import org.example.bankcards.service.UserService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "Operations an users")
public class UserController {

    private final UserService userService;
    private final CardService cardService;

    @Operation(summary = "Get this profile")
    @GetMapping("/profile")
    public ResponseEntity<UserResponseDTO> getUserById(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserResponseDTO response = userService.getUserById(userDetails.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get user's cards with filtering and pagination")
    @GetMapping
    public ResponseEntity<Page<CardSimpleResponseDTO>> getUserCards(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetailsImpl user,
            @ParameterObject CardFilterRequestDTO filter,
            @ParameterObject Pageable pageable
    ) {
        Page<CardSimpleResponseDTO> cards = cardService.getUserCards(user.getId(), filter, pageable);
        return ResponseEntity.ok(cards);
    }
}
