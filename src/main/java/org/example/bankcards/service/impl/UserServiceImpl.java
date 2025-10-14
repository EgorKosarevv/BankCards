package org.example.bankcards.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bankcards.dto.user.request.UserFilterRequestDTO;
import org.example.bankcards.dto.user.request.UserStatusUpdateRequestDTO;
import org.example.bankcards.dto.user.response.UserResponseDTO;
import org.example.bankcards.entity.RoleEntity;
import org.example.bankcards.entity.UserEntity;
import org.example.bankcards.entity.UserRole;
import org.example.bankcards.exception.ConflictException;
import org.example.bankcards.exception.NotFoundException;
import org.example.bankcards.exception.ServiceException;
import org.example.bankcards.mapper.UserMapper;
import org.example.bankcards.repository.RefreshTokenRepository;
import org.example.bankcards.repository.UserRepository;
import org.example.bankcards.repository.specification.UserSpecifications;
import org.example.bankcards.service.RoleService;
import org.example.bankcards.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoleService roleService;

    @Override
    public UserEntity getUserEntityById(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("The user with ID " + userId + " not found"));

    }

    @Override
    public UserResponseDTO getUserById(long userId) {
        UserEntity userEntity = getUserEntityById(userId);
        return userMapper.toResponseDTO(userEntity);
    }


    @Override
    public UserEntity getUserEntityByPhone(String phone) {
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new NotFoundException("The user with phone " + phone + " not found."));
    }

    @Override
    public void updateUserStatus(long userId, UserStatusUpdateRequestDTO request, long currentUserId) {
        if (userId == currentUserId) {
            throw new ServiceException("are you trying to update the current user");
        }
        UserEntity userEntity = getUserEntityById(userId);

        boolean enable = request.isEnable();

        if (userEntity.isEnabled() == enable) {
            throw new ConflictException("The user already has the status " + enable);
        }

        userEntity.setEnabled(enable);
        userRepository.save(userEntity);

        if (!request.isEnable()) {
            refreshTokenRepository.deleteByUser(userEntity);
        }
        log.info("the status of the user with ID {} has been updated", userId);
    }


    @Override
    public void updateUserRoles(long userId, Set<String> roles, long currentUserId) {
        if (userId == currentUserId) {
            throw new ServiceException("are you trying to update the current user");
        }
        UserEntity user = getUserEntityById(userId);

        Set<RoleEntity> updatedRoles = new HashSet<>();
        for (String roleName : roles) {
            RoleEntity role = roleService.getRoleByName(UserRole.valueOf(roleName));
            updatedRoles.add(role);
        }

        user.setRoles(updatedRoles);
        userRepository.save(user);

        log.info("The roles of the user with ID {} have been successfully updated ", userId);
    }

    @Override
    public Page<UserResponseDTO> getAllUsers(UserFilterRequestDTO filter, Pageable pageable) {
        Specification<UserEntity> spec = UserSpecifications.filterBy(filter);
        return userRepository.findAll(spec, pageable)
                .map(userMapper::toResponseDTO);
    }


}
