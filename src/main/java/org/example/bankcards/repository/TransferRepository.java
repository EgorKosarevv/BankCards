package org.example.bankcards.repository;

import org.example.bankcards.entity.TransferEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<TransferEntity, Long> {

    @Query("""
        SELECT t FROM TransferEntity t 
        WHERE t.fromCard.id = :cardId OR t.toCard.id = :cardId
        ORDER BY t.createdAt DESC
    """)
    Page<TransferEntity> findAllByCardId(@Param("cardId") Long cardId, Pageable pageable);
}
