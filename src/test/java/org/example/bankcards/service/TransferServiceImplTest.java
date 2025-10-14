package org.example.bankcards.service;

import org.example.bankcards.dto.card.transfer.request.CardTransferRequestDTO;
import org.example.bankcards.dto.card.transfer.response.TransferResponseDTO;
import org.example.bankcards.entity.CardEntity;
import org.example.bankcards.entity.CardStatus;
import org.example.bankcards.entity.TransferEntity;
import org.example.bankcards.entity.UserEntity;
import org.example.bankcards.exception.ServiceException;
import org.example.bankcards.mapper.TransferMapper;
import org.example.bankcards.repository.CardRepository;
import org.example.bankcards.repository.TransferRepository;
import org.example.bankcards.service.impl.TransferServiceImpl;
import org.example.bankcards.util.TestObjectUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private TransferRepository transferRepository;
    @Mock
    private CardService cardService;
    @Mock
    private TransferMapper transferMapper;

    @InjectMocks
    private TransferServiceImpl transferService;

    private final CardEntity fromCard = TestObjectUtils.createCardEntity();
    private final CardEntity toCard = TestObjectUtils.createCardEntity();
    private final UserEntity userEntity = TestObjectUtils.createUserEntity();
    private final CardTransferRequestDTO transferRequestDTO = TestObjectUtils.createCardTransferRequestDTO();
    private final TransferEntity transferEntity = TestObjectUtils.createTransferEntity();
    private final TransferResponseDTO transferResponseDTO = TestObjectUtils.createTransferResponseDTO();

    @BeforeEach
    void setUp() {
        fromCard.setId(1L);
        toCard.setId(2L);
        fromCard.setOwner(userEntity);
        toCard.setOwner(userEntity);
        fromCard.setBalance(BigDecimal.valueOf(100));
        toCard.setBalance(BigDecimal.ZERO);
        fromCard.setStatus(CardStatus.ACTIVE);
        toCard.setStatus(CardStatus.ACTIVE);

        transferRequestDTO.setFromCardId(1L);
        transferRequestDTO.setToCardId(2L);
        transferRequestDTO.setAmount(BigDecimal.valueOf(50));
    }

    @Test
    void shouldTransferBetweenOwnCardsTest() {
        // given


        // when
        when(cardRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toCard));
        when(transferMapper.toEntity(transferRequestDTO)).thenReturn(transferEntity);
        transferService.transferBetweenOwnCards(userEntity.getId(), transferRequestDTO);

        // then
        assertEquals(BigDecimal.valueOf(50), fromCard.getBalance());
        assertEquals(BigDecimal.valueOf(50), toCard.getBalance());

        verify(cardRepository).save(fromCard);
        verify(cardRepository).save(toCard);
        verify(transferRepository).save(any(TransferEntity.class));
    }

    @Test
    void shouldThrowWhenNotEnoughFundsTest() {
        // given
        transferRequestDTO.setAmount(BigDecimal.valueOf(200));

        // when
        when(cardRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdForUpdate(2L)).thenReturn(Optional.of(toCard));
        ServiceException exception = assertThrows(ServiceException.class,
                () -> transferService.transferBetweenOwnCards(userEntity.getId(), transferRequestDTO));

        // then
        assertTrue(exception.getMessage().contains("Insufficient funds"));
    }

    @Test
    void shouldThrowWhenCardsSameTest() {
        // given
        transferRequestDTO.setToCardId(1L);

        // when
        when(cardRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromCard));
        when(cardRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(fromCard));
        ServiceException exception = assertThrows(ServiceException.class,
                () -> transferService.transferBetweenOwnCards(userEntity.getId(), transferRequestDTO));

        // then
        assertTrue(exception.getMessage().contains("same card"));
    }

    @Test
    void shouldGetTransfersForCardTest() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<TransferEntity> page = new PageImpl<>(List.of(transferEntity));


        // when
        when(cardService.getCardEntityById(1L)).thenReturn(fromCard);
        when(transferRepository.findAllByCardId(1L, pageable)).thenReturn(page);
        when(transferMapper.toResponseDTO(transferEntity)).thenReturn(transferResponseDTO);
        Page<TransferResponseDTO> result = transferService.getTransfersForCard(1L, pageable, userEntity.getId());

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(transferRepository).findAllByCardId(1L, pageable);
        verify(transferMapper).toResponseDTO(transferEntity);
    }

}
