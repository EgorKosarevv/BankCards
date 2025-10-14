package org.example.bankcards.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.bankcards.dto.card.transfer.request.CardTransferRequestDTO;
import org.example.bankcards.dto.card.transfer.response.TransferResponseDTO;
import org.example.bankcards.entity.CardEntity;
import org.example.bankcards.entity.CardStatus;
import org.example.bankcards.entity.TransferEntity;
import org.example.bankcards.exception.NotFoundException;
import org.example.bankcards.exception.ServiceException;
import org.example.bankcards.mapper.TransferMapper;
import org.example.bankcards.repository.CardRepository;
import org.example.bankcards.repository.TransferRepository;
import org.example.bankcards.service.CardService;
import org.example.bankcards.service.TransferService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

    private final CardRepository cardRepository;
    private final TransferRepository transferRepository;
    private final CardService cardService;
    private final TransferMapper transferMapper;

    @Override
    @Transactional
    public void transferBetweenOwnCards(Long userId, CardTransferRequestDTO request) {
        CardEntity fromCard = cardRepository.findByIdForUpdate(request.getFromCardId())
                .orElseThrow(() -> new NotFoundException("Source card not found"));

        CardEntity toCard = cardRepository.findByIdForUpdate(request.getToCardId())
                .orElseThrow(() -> new NotFoundException("Target card not found"));

        if (!fromCard.getOwner().getId().equals(userId) || !toCard.getOwner().getId().equals(userId)) {
            throw new ServiceException("You can transfer only between your own cards");
        }

        if (fromCard.getStatus() != CardStatus.ACTIVE || toCard.getStatus() != CardStatus.ACTIVE) {
            throw new ServiceException("Both cards must be ACTIVE for transfer");
        }

        if (fromCard.getId().equals(toCard.getId())) {
            throw new ServiceException("Cannot transfer to the same card");
        }

        if (fromCard.getBalance().compareTo(request.getAmount()) < 0) {
            throw new ServiceException("Insufficient funds on the source card");
        }

        fromCard.setBalance(fromCard.getBalance().subtract(request.getAmount()));
        toCard.setBalance(toCard.getBalance().add(request.getAmount()));

        cardRepository.save(fromCard);
        cardRepository.save(toCard);

        TransferEntity transfer = transferMapper.toEntity(request);
        transfer.setFromCard(fromCard);
        transfer.setToCard(toCard);

        transferRepository.save(transfer);

        log.info("User {} transferred {} from card {} to card {}",
                userId, request.getAmount(), fromCard.getId(), toCard.getId());
    }

    @Override
    public Page<TransferResponseDTO> getTransfersForCard(Long cardId, Pageable pageable, Long userId) {
        CardEntity card = cardService.getCardEntityById(cardId);

        if (!card.getOwner().getId().equals(userId)) {
            throw new ServiceException("You can only view transfers for your own cards");
        }

        Page<TransferEntity> transfers = transferRepository.findAllByCardId(cardId, pageable);
        return transfers.map(transferMapper::toResponseDTO);
    }
}