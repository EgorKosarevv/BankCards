package org.example.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankcards.dto.card.request.CardFilterRequestDTO;
import org.example.bankcards.dto.card.request.CardRequestDTO;
import org.example.bankcards.dto.card.request.CardStatusUpdateRequestDTO;
import org.example.bankcards.dto.card.response.CardSimpleResponseDTO;
import org.example.bankcards.entity.CardStatus;
import org.example.bankcards.exception.ServiceException;
import org.example.bankcards.security.JwtAuthFilter;
import org.example.bankcards.service.CardService;
import org.example.bankcards.util.TestObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminCardController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminCardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    private final CardRequestDTO cardRequestDTO = TestObjectUtils.createCardRequestDTO();
    private final CardStatusUpdateRequestDTO statusUpdateRequestDTO = TestObjectUtils.createCardStatusUpdateRequestDTO();
    private final CardSimpleResponseDTO cardSimpleResponseDTO = TestObjectUtils.createCardSimpleResponseDTO();

    @Test
    void shouldCreateCardTest() throws Exception {
        // given

        // when
        doNothing().when(cardService).createCard(cardRequestDTO);

        // then
        mockMvc.perform(post("/api/v1/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cardRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotCreateCardWhenInvalidRequestTest() throws Exception {
        // given
        CardRequestDTO invalidRequest = new CardRequestDTO();

        // when / then
        mockMvc.perform(post("/api/v1/admin/cards")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllCardsTest() throws Exception {
        // given
        Page<CardSimpleResponseDTO> page = new PageImpl<>(List.of(cardSimpleResponseDTO));

        // when
        when(cardService.getAllCards(any(CardFilterRequestDTO.class), any(Pageable.class))).thenReturn(page);

        // then
        mockMvc.perform(get("/api/v1/admin/cards")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldChangeCardStatusTest() throws Exception {
        // given
        Long cardId = 1L;
        statusUpdateRequestDTO.setStatus(CardStatus.BLOCKED);

        // when
        doNothing().when(cardService).changeCardStatus(cardId, statusUpdateRequestDTO);

        // then
        mockMvc.perform(put("/api/v1/admin/cards/{cardId}/status", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusUpdateRequestDTO)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldNotChangeCardStatusWhenInvalidTest() throws Exception {
        // given
        Long cardId = 1L;
        CardStatusUpdateRequestDTO invalidRequest = new CardStatusUpdateRequestDTO();

        // when / then
        mockMvc.perform(put("/api/v1/admin/cards/{cardId}/status", cardId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteCardTest() throws Exception {
        // given
        Long cardId = 1L;

        // when
        doNothing().when(cardService).deleteCard(cardId);

        // then
        mockMvc.perform(delete("/api/v1/admin/cards/{cardId}", cardId))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnBadRequestWhenDeleteFailsTest() throws Exception {
        // given
        Long cardId = 1L;

        // when
        doThrow(new ServiceException("Cannot delete card")).when(cardService).deleteCard(cardId);

        //  then
        mockMvc.perform(delete("/api/v1/admin/cards/{cardId}", cardId))
                .andExpect(status().isBadRequest());
    }
}
