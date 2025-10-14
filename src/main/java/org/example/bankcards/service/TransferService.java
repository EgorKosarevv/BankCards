package org.example.bankcards.service;

import org.example.bankcards.dto.card.transfer.request.CardTransferRequestDTO;
import org.example.bankcards.dto.card.transfer.response.TransferResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransferService {
    void transferBetweenOwnCards(Long userId, CardTransferRequestDTO request);
    Page<TransferResponseDTO> getTransfersForCard(Long cardId, Pageable pageable, Long userId);
}
