package org.example.bankcards.repository.specification;

import org.example.bankcards.dto.card.request.CardFilterRequestDTO;
import org.example.bankcards.entity.CardEntity;
import org.example.bankcards.entity.CardStatus;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class CardSpecifications {
    public static Specification<CardEntity> filterBy(CardFilterRequestDTO filter) {
        return Specification.where(hasOwner(filter.getOwnerId()))
                .and(hasLast4(filter.getLast4()))
                .and(hasStatus(filter.getStatus()))
                .and(hasBlockRequest(filter.getBlockRequested()))
                .and(hasBalanceBetween(filter.getMinBalance(), filter.getMaxBalance()));
    }

    private static Specification<CardEntity> hasOwner(Long ownerId) {
        return (root, query, cb) ->
                ownerId == null ? cb.conjunction() : cb.equal(root.get("owner").get("id"), ownerId);
    }

    private static Specification<CardEntity> hasLast4(String last4) {
        return (root, query, cb) ->
                (last4 == null || last4.isBlank()) ? cb.conjunction() : cb.like(root.get("last4"), "%" + last4 + "%");
    }

    private static Specification<CardEntity> hasStatus(CardStatus status) {
        return (root, query, cb) ->
                status == null ? cb.conjunction() : cb.equal(root.get("status"), status);
    }

    private static Specification<CardEntity> hasBlockRequest(Boolean blockRequested) {
        return (root, query, cb) ->
                blockRequested == null ? cb.conjunction() : cb.equal(root.get("blockRequested"), blockRequested);
    }
    private static Specification<CardEntity> hasBalanceBetween(BigDecimal minBalance, BigDecimal maxBalance) {
        return (root, query, cb) -> {
            if (minBalance == null && maxBalance == null) return cb.conjunction();

            if (minBalance != null && maxBalance != null) {
                return cb.between(root.get("balance"), minBalance, maxBalance);
            } else if (minBalance != null) {
                return cb.greaterThanOrEqualTo(root.get("balance"), minBalance);
            } else {
                return cb.lessThanOrEqualTo(root.get("balance"), maxBalance);
            }
        };
    }
}
