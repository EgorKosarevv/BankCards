package org.example.bankcards.repository;

import org.example.bankcards.entity.RefreshTokenEntity;
import org.example.bankcards.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);
    Integer  deleteByExpiryDateBefore(Instant instant);
    void deleteByUser(UserEntity user);
}
