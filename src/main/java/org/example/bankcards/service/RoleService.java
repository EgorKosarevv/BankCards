package org.example.bankcards.service;

import org.example.bankcards.entity.RoleEntity;
import org.example.bankcards.entity.UserRole;

public interface RoleService {
    RoleEntity getRoleByName(UserRole roleName);
}
