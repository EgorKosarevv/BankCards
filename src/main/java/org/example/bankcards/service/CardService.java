package org.example.bankcards.service;

import org.example.bankcards.dto.card.request.CardDepositRequestDTO;
import org.example.bankcards.dto.card.request.CardFilterRequestDTO;
import org.example.bankcards.dto.card.request.CardRequestDTO;
import org.example.bankcards.dto.card.request.CardStatusUpdateRequestDTO;
import org.example.bankcards.dto.card.response.CardResponseDTO;
import org.example.bankcards.dto.card.response.CardSimpleResponseDTO;
import org.example.bankcards.entity.CardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CardService {
    CardResponseDTO getCardById(long cardId);
    CardEntity getCardEntityById(long cardId);
    void createCard(CardRequestDTO request);
    void deleteCard(long cardId);
    void changeCardStatus(long cardId, CardStatusUpdateRequestDTO request);
    void markExpiredCards();
    void requestBlock(long userId, long cardId);
    void depositToCard(CardDepositRequestDTO request, Long userId);
    Page<CardSimpleResponseDTO> getAllCards(CardFilterRequestDTO filter, Pageable pageable);
    Page<CardSimpleResponseDTO> getUserCards(Long userId, CardFilterRequestDTO filter, Pageable pageable);

}
