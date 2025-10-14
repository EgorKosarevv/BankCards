package org.example.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankcards.dto.card.request.CardDepositRequestDTO;
import org.example.bankcards.exception.ServiceException;
import org.example.bankcards.security.JwtAuthFilter;
import org.example.bankcards.security.UserDetailsImpl;
import org.example.bankcards.service.CardService;
import org.example.bankcards.util.TestObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardController.class)
@AutoConfigureMockMvc(addFilters = false)
class CardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    private final UserDetailsImpl userDetails = TestObjectUtils.createUserDetailsImpl();
    private final CardDepositRequestDTO cardDepositRequestDTO = TestObjectUtils.createCardDepositRequestDTO();

    @Test
    void shouldRequestCardBlockTest() throws Exception {
        // given
        Long cardId = 1L;

        // when
        doNothing().when(cardService).requestBlock(userDetails.getId(), cardId);

        // then
        mockMvc.perform(put("/api/v1/cards/{cardId}/block-request", cardId)
                        .principal(() -> "user")
                        .requestAttr("userDetails", userDetails))
                .andExpect(status().isAccepted());
    }

    @Test
    void shouldDepositToCardTest() throws Exception {
        // given
        cardDepositRequestDTO.setCardId(1L);
        cardDepositRequestDTO.setAmount(BigDecimal.valueOf(500));

        // when
        doNothing().when(cardService).depositToCard(cardDepositRequestDTO, userDetails.getId());

        // then
        mockMvc.perform(put("/api/v1/cards/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDepositRequestDTO))
                        .principal(() -> "user")
                        .requestAttr("userDetails", userDetails))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnBadRequestWhenDepositInvalidTest() throws Exception {
        // given
        cardDepositRequestDTO.setCardId(1L);
        cardDepositRequestDTO.setAmount(BigDecimal.valueOf(-100));

        // when / then
        mockMvc.perform(put("/api/v1/cards/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardDepositRequestDTO))
                        .principal(() -> "user")
                        .requestAttr("userDetails", userDetails))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequestWhenCardBlockFailsTest() throws Exception {
        // given
        Long cardId = 1L;
        doThrow(new ServiceException("Cannot block card"))
                .when(cardService).requestBlock(userDetails.getId(), cardId);

        // when / then
        mockMvc.perform(put("/api/v1/cards/{cardId}/block-request", cardId)
                        .principal(() -> "user")
                        .requestAttr("userDetails", userDetails))
                .andExpect(status().isBadRequest());
    }
}
