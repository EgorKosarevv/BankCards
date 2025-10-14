package org.example.bankcards.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bankcards.dto.card.request.CardDepositRequestDTO;
import org.example.bankcards.dto.card.request.CardFilterRequestDTO;
import org.example.bankcards.dto.card.request.CardRequestDTO;
import org.example.bankcards.dto.card.request.CardStatusUpdateRequestDTO;
import org.example.bankcards.dto.card.response.CardResponseDTO;
import org.example.bankcards.dto.card.response.CardSimpleResponseDTO;
import org.example.bankcards.entity.CardEntity;
import org.example.bankcards.entity.CardStatus;
import org.example.bankcards.entity.UserEntity;
import org.example.bankcards.exception.NotFoundException;
import org.example.bankcards.exception.ServiceException;
import org.example.bankcards.mapper.CardMapper;
import org.example.bankcards.repository.CardRepository;
import org.example.bankcards.repository.specification.CardSpecifications;
import org.example.bankcards.service.CardService;
import org.example.bankcards.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;
    private final UserService userService;
    private final CardMapper cardMapper;

    @Value("${cards.expiry-years:5}")
    private int expiryYears;


    @Override
    public CardEntity getCardEntityById(long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new NotFoundException("The card with ID " + cardId + " not found"));

    }

    @Override
    public CardResponseDTO getCardById(long cardId) {
        CardEntity cardEntity = getCardEntityById(cardId);
        return cardMapper.toResponseDTO(cardEntity);
    }

    @Override
    public void createCard(CardRequestDTO request) {
        UserEntity owner = userService.getUserEntityById(request.getOwnerId());
        CardEntity cardEntity = cardMapper.toEntity(request);

        cardEntity.setOwner(owner);
        String last4 = String.format("%04d", ThreadLocalRandom.current().nextInt(0, 10_000));
        cardEntity.setLast4(last4);
        cardEntity.setExpiry(LocalDateTime.now().plusYears(expiryYears));

        cardRepository.save(cardEntity);
        log.info("The card has been created: {}", cardEntity);
    }

    @Override
    public void deleteCard(long cardId) {
        CardEntity cardEntity = getCardEntityById(cardId);

        if (CardStatus.DELETED == cardEntity.getStatus()) {
            throw new ServiceException("The card has already been deleted");
        }
        if (cardEntity.getBalance().compareTo(BigDecimal.ZERO) > 0) {
            throw new ServiceException("Cannot delete a card with non-zero balance");
        }

        cardEntity.setStatus(CardStatus.DELETED);
        cardRepository.save(cardEntity);
        log.info("The card with ID {} has been deleted", cardId);
    }

    @Override
    public void changeCardStatus(long cardId, CardStatusUpdateRequestDTO request) {
        CardEntity cardEntity = getCardEntityById(cardId);
        CardStatus newStatus = request.getStatus();

        if (cardEntity.getStatus() != CardStatus.ACTIVE && cardEntity.getStatus() != CardStatus.BLOCKED) {
            throw new ServiceException("the card status does not match (only ACTIVE, BLOCKED or BLOCKED_REQUESTED)");
        }

        if (!(newStatus == CardStatus.ACTIVE || newStatus == CardStatus.BLOCKED)) {
            throw new ServiceException("Only ACTIVE and BLOCKED statuses can be set manually");
        }

        if (newStatus == CardStatus.ACTIVE && cardEntity.getExpiry().isBefore(LocalDateTime.now())) {
            throw new ServiceException("Cannot activate an expired card");
        }

        if (cardEntity.getStatus() == newStatus) {
            throw new ServiceException("Card is already in status: " + newStatus);
        }
        if (cardEntity.isBlockRequested()) {
            cardEntity.setBlockRequested(false);
        }
        cardEntity.setStatus(newStatus);
        cardRepository.save(cardEntity);

        log.info("Card {} status changed to {}", cardId, newStatus);
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    @Override
    public void markExpiredCards() {
        LocalDateTime now = LocalDateTime.now();

        int updated = cardRepository.updateExpiredCards(now);
        log.info("Marked {} cards as expired", updated);

    }

    @Override
    public void requestBlock(long userId, long cardId) {
        CardEntity cardEntity = getCardEntityById(cardId);

        if (!cardEntity.getOwner().getId().equals(userId)) {
            throw new ServiceException("You can request block only for your own card");
        }
        if (cardEntity.getStatus() != CardStatus.ACTIVE) {
            throw new ServiceException("Only active cards can be blocked");
        }
        if (cardEntity.isBlockRequested()) {
            throw new ServiceException("the request already exists");
        }
        cardEntity.setBlockRequested(true);
        cardRepository.save(cardEntity);
        log.info("User {} requested block for card {}.", userId, cardId);
    }

    @Override
    @Transactional
    public void depositToCard(CardDepositRequestDTO request, Long userId) {
        CardEntity card = cardRepository.findByIdForUpdate(request.getCardId())
                .orElseThrow(() -> new NotFoundException("Card not found"));

        if (!card.getOwner().getId().equals(userId)) {
            throw new ServiceException("You can deposit only to your own cards");
        }

        if (card.getStatus() != CardStatus.ACTIVE) {
            throw new ServiceException("Only active cards can be credited");
        }

        BigDecimal newBalance = card.getBalance().add(request.getAmount());
        card.setBalance(newBalance);

        cardRepository.save(card);
        log.info("User {} deposited {} to card {}", userId, request.getAmount(), card.getId());
    }


    @Override
    public Page<CardSimpleResponseDTO> getAllCards(CardFilterRequestDTO filter, Pageable pageable) {
        Specification<CardEntity> spec = CardSpecifications.filterBy(filter);
        return cardRepository.findAll(spec, pageable)
                .map(cardMapper::toSimpleDTO);
    }

    @Override
    public Page<CardSimpleResponseDTO> getUserCards(Long userId, CardFilterRequestDTO filter, Pageable pageable) {
        filter.setOwnerId(userId);
        Specification<CardEntity> spec = CardSpecifications.filterBy(filter);
        return cardRepository.findAll(spec, pageable)
                .map(cardMapper::toSimpleDTO);
    }
}
