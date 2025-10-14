package org.example.bankcards.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bankcards.dto.user.request.UserFilterRequestDTO;
import org.example.bankcards.dto.user.request.UserRoleUpdateRequestDTO;
import org.example.bankcards.dto.user.request.UserStatusUpdateRequestDTO;
import org.example.bankcards.dto.user.response.UserResponseDTO;
import org.example.bankcards.security.UserDetailsImpl;
import org.example.bankcards.service.UserService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Tag(name = "AdminUser", description = "Admin operations an users")
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "Get profile by Id")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDTO> getUserById( @PathVariable Long userId) {
        UserResponseDTO response = userService.getUserById(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update user status", description = "Allows admins to change the status of a user (active, blocked)")
    @PutMapping("/{userId}/status")
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long userId,
                                                 @AuthenticationPrincipal UserDetailsImpl userDetails,
                                                 @RequestBody @Valid UserStatusUpdateRequestDTO request) {
        userService.updateUserStatus(userId, request, userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Update user roles", description = "Allows admins to change user roles")
    @PutMapping("/{userId}/roles")
    public ResponseEntity<Void> updateUserRoles(
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody @Valid UserRoleUpdateRequestDTO requestDTO) {

        userService.updateUserRoles(userId, requestDTO.getRoles(), userDetails.getId());
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @Operation(summary = "Get all users with filtering and pagination")
    public ResponseEntity<Page<UserResponseDTO>> getAllUsers(
            @Valid @ParameterObject UserFilterRequestDTO filter,
            @ParameterObject Pageable pageable
    ) {
        Page<UserResponseDTO> users = userService.getAllUsers(filter, pageable);
        return ResponseEntity.ok(users);
    }

}
