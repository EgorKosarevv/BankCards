package org.example.bankcards.repository.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.example.bankcards.dto.user.request.UserFilterRequestDTO;
import org.example.bankcards.entity.RoleEntity;
import org.example.bankcards.entity.UserEntity;
import org.example.bankcards.entity.UserRole;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

    public static Specification<UserEntity> filterBy(UserFilterRequestDTO filter) {
        return Specification.where(fullNameContains(filter.getFullName()))
                .and(phoneContains(filter.getPhone()))
                .and(hasRole(filter.getRole()))
                .and(isEnabled(filter.getEnabled()));
    }

    private static Specification<UserEntity> fullNameContains(String fullName) {
        return (root, query, cb) ->
                (fullName == null || fullName.isBlank())
                        ? cb.conjunction()
                        : cb.like(cb.lower(root.get("fullName")), "%" + fullName.toLowerCase() + "%");
    }

    private static Specification<UserEntity> phoneContains(String phone) {
        return (root, query, cb) ->
                (phone == null || phone.isBlank())
                        ? cb.conjunction()
                        : cb.like(root.get("phone"), "%" + phone + "%");
    }

    private static Specification<UserEntity> hasRole(UserRole role) {
        return (root, query, cb) -> {
            if (role == null) return cb.conjunction();
            Join<UserEntity, RoleEntity> roles = root.join("roles", JoinType.LEFT);
            return cb.equal(roles.get("roleName"), role);
        };
    }

    private static Specification<UserEntity> isEnabled(Boolean enabled) {
        return (root, query, cb) ->
                enabled == null ? cb.conjunction() : cb.equal(root.get("enabled"), enabled);
    }
}
