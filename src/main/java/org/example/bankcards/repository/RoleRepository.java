package org.example.bankcards.repository;

import org.example.bankcards.entity.RoleEntity;
import org.example.bankcards.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByRoleName(UserRole roleName);

    boolean existsByRoleName(UserRole roleName);
}