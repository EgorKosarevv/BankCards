package org.example.bankcards.service;

import org.example.bankcards.dto.user.request.UserFilterRequestDTO;
import org.example.bankcards.dto.user.request.UserStatusUpdateRequestDTO;

import org.example.bankcards.dto.user.response.UserResponseDTO;
import org.example.bankcards.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface UserService {
    UserResponseDTO getUserById(long userId);
    UserEntity getUserEntityById(long userId);
    UserEntity getUserEntityByPhone(String phone);
    void updateUserStatus(long userId, UserStatusUpdateRequestDTO request, long currentUserId);
    void updateUserRoles(long userId, Set<String> roles, long currentUserId);
    Page<UserResponseDTO> getAllUsers(UserFilterRequestDTO filter, Pageable pageable);

}
