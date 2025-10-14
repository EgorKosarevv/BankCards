package org.example.bankcards.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bankcards.entity.RoleEntity;
import org.example.bankcards.entity.UserRole;
import org.example.bankcards.exception.NotFoundException;
import org.example.bankcards.repository.RoleRepository;
import org.example.bankcards.service.RoleService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleEntity getRoleByName(UserRole roleName) {
        return roleRepository.findByRoleName(roleName)
                .orElseThrow(() -> new NotFoundException("The role " + roleName + " not found."));
    }
}
