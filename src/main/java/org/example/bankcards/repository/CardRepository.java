package org.example.bankcards.repository;

import jakarta.persistence.LockModeType;
import org.example.bankcards.entity.CardEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<CardEntity, Long>, JpaSpecificationExecutor<CardEntity> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CardEntity c WHERE c.id = :id")
    Optional<CardEntity> findByIdForUpdate(@Param("id") Long id);

    @Modifying
    @Query("""
    UPDATE CardEntity c
    SET c.status = 'EXPIRED'
    WHERE c.expiry < :now AND c.status <> 'EXPIRED'
""")
    int updateExpiredCards(@Param("now") LocalDateTime now);
}
