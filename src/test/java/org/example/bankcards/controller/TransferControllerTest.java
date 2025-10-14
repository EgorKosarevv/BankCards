package org.example.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankcards.dto.card.transfer.request.CardTransferRequestDTO;
import org.example.bankcards.dto.card.transfer.response.TransferResponseDTO;
import org.example.bankcards.security.JwtAuthFilter;
import org.example.bankcards.security.UserDetailsImpl;
import org.example.bankcards.service.TransferService;
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

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransferService transferService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    private final UserDetailsImpl userDetails = TestObjectUtils.createUserDetailsImpl();
    private final CardTransferRequestDTO transferRequestDTO = TestObjectUtils.createCardTransferRequestDTO();
    private final TransferResponseDTO transferResponseDTO = TestObjectUtils.createTransferResponseDTO();

    @Test
    void shouldTransferBetweenOwnCardsTest() throws Exception {
        // given
        transferRequestDTO.setFromCardId(1L);
        transferRequestDTO.setToCardId(2L);
        transferRequestDTO.setAmount(BigDecimal.valueOf(100));

        // when
        doNothing().when(transferService).transferBetweenOwnCards(userDetails.getId(), transferRequestDTO);

        // then
        mockMvc.perform(post("/api/v1/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequestDTO))
                        .principal(() -> "user")
                        .requestAttr("userDetails", userDetails))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldGetCardTransfersTest() throws Exception {
        // given
        Page<TransferResponseDTO> transfers = new PageImpl<>(List.of(transferResponseDTO));

        // when
        when(transferService.getTransfersForCard(eq(1L), any(Pageable.class), eq(userDetails.getId())))
                .thenReturn(transfers);

        // then
        mockMvc.perform(get("/api/v1/transfers/card/{cardId}", 1L)
                        .param("page", "0")
                        .param("size", "10")
                        .principal(() -> "user")
                        .requestAttr("userDetails", userDetails))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnEmptyPageWhenNoTransfersTest() throws Exception {
        // given
        Page<TransferResponseDTO> emptyPage = Page.empty();

        // when
        when(transferService.getTransfersForCard(eq(1L), any(Pageable.class), eq(userDetails.getId())))
                .thenReturn(emptyPage);

        // then
        mockMvc.perform(get("/api/v1/transfers/card/{cardId}", 1L)
                        .param("page", "0")
                        .param("size", "10")
                        .principal(() -> "user")
                        .requestAttr("userDetails", userDetails))
                .andExpect(status().isOk());
    }
}
