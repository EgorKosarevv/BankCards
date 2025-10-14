package org.example.bankcards.service;

import org.example.bankcards.dto.card.request.CardDepositRequestDTO;
import org.example.bankcards.dto.card.request.CardFilterRequestDTO;
import org.example.bankcards.dto.card.request.CardRequestDTO;
import org.example.bankcards.dto.card.request.CardStatusUpdateRequestDTO;
import org.example.bankcards.dto.card.response.CardResponseDTO;
import org.example.bankcards.entity.CardEntity;
import org.example.bankcards.entity.CardStatus;
import org.example.bankcards.entity.UserEntity;
import org.example.bankcards.exception.NotFoundException;
import org.example.bankcards.exception.ServiceException;
import org.example.bankcards.mapper.CardMapper;
import org.example.bankcards.repository.CardRepository;
import org.example.bankcards.service.impl.CardServiceImpl;
import org.example.bankcards.util.TestObjectUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;
    @Mock
    private UserService userService;
    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private final CardEntity cardEntity = TestObjectUtils.createCardEntity();
    private final CardResponseDTO cardResponseDTO = TestObjectUtils.createCardResponseDTO();
    private final CardRequestDTO cardRequestDTO = TestObjectUtils.createCardRequestDTO();
    private final CardStatusUpdateRequestDTO cardStatusUpdateRequestDTO = TestObjectUtils.createCardStatusUpdateRequestDTO();
    private final CardDepositRequestDTO cardDepositRequestDTO = TestObjectUtils.createCardDepositRequestDTO();
    private final CardFilterRequestDTO cardFilterRequestDTO = TestObjectUtils.createCardFilterRequestDTO();
    private final UserEntity userEntity = TestObjectUtils.createUserEntity();

    @Test
    void shouldGetCardEntityByIdTest() {
        // given


        // when
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        CardEntity result = cardService.getCardEntityById(1);

        // then
        assertNotNull(result);
        assertEquals(cardEntity, result);
        verify(cardRepository).findById(1L);
    }

    @Test
    void shouldNotGetCardEntityByIdWhenNotFoundTest() {
        // given
        String expected = "not found";

        // when
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> cardService.getCardEntityById(1));

        // then
        assertTrue(exception.getMessage().contains(expected));
        verify(cardRepository).findById(1L);
    }

    @Test
    void shouldGetCardByIdTest() {
        // given

        // when
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        when(cardMapper.toResponseDTO(cardEntity)).thenReturn(cardResponseDTO);
        CardResponseDTO result = cardService.getCardById(1);

        // then
        assertEquals(cardResponseDTO, result);
        verify(cardRepository).findById(1L);
        verify(cardMapper).toResponseDTO(cardEntity);
    }

    @Test
    void shouldCreateCardTest() {
        // given


        // when
        when(userService.getUserEntityById(cardRequestDTO.getOwnerId())).thenReturn(userEntity);
        when(cardMapper.toEntity(cardRequestDTO)).thenReturn(cardEntity);
        cardService.createCard(cardRequestDTO);

        // then
        verify(cardRepository).save(cardEntity);
    }

    @Test
    void shouldDeleteCardTest() {
        // given
        cardEntity.setStatus(CardStatus.ACTIVE);
        cardEntity.setBalance(BigDecimal.ZERO);

        // when
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        cardService.deleteCard(1L);

        // then
        verify(cardRepository).save(cardEntity);
        assertEquals(CardStatus.DELETED, cardEntity.getStatus());
    }

    @Test
    void shouldThrowWhenDeletingCardWithBalanceTest() {
        // given
        cardEntity.setBalance(BigDecimal.TEN);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));

        // when / then
        ServiceException exception = assertThrows(ServiceException.class, () -> cardService.deleteCard(1L));
        assertTrue(exception.getMessage().contains("non-zero balance"));
    }

    @Test
    void shouldChangeCardStatusTest() {
        // given
        cardEntity.setStatus(CardStatus.ACTIVE);
        cardStatusUpdateRequestDTO.setStatus(CardStatus.BLOCKED);

        // when
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        when(cardRepository.save(cardEntity)).thenReturn(cardEntity);
        cardService.changeCardStatus(1L, cardStatusUpdateRequestDTO);

        // then
        verify(cardRepository).save(cardEntity);
        assertEquals(CardStatus.BLOCKED, cardEntity.getStatus());
    }

    @Test
    void shouldDepositToCardTest() {
        // given
        cardEntity.setStatus(CardStatus.ACTIVE);
        cardEntity.setBalance(BigDecimal.ZERO);

        cardDepositRequestDTO.setCardId(1L);
        cardDepositRequestDTO.setAmount(BigDecimal.TEN);

        // when
        when(cardRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(cardEntity));
        cardService.depositToCard(cardDepositRequestDTO, userEntity.getId());

        // then
        verify(cardRepository).save(cardEntity);
        assertEquals(BigDecimal.TEN, cardEntity.getBalance());
    }

    @Test
    void shouldRequestBlockTest() {
        // given
        cardEntity.setStatus(CardStatus.ACTIVE);
        cardEntity.setBlockRequested(false);

        // when
        when(cardRepository.findById(1L)).thenReturn(Optional.of(cardEntity));
        cardService.requestBlock(userEntity.getId(), 1L);

        // then
        verify(cardRepository).save(cardEntity);
        assertTrue(cardEntity.isBlockRequested());
    }
}
