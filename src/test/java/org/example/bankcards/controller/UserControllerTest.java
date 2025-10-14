package org.example.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankcards.dto.card.request.CardFilterRequestDTO;
import org.example.bankcards.dto.card.response.CardSimpleResponseDTO;
import org.example.bankcards.dto.user.response.UserResponseDTO;
import org.example.bankcards.security.JwtAuthFilter;
import org.example.bankcards.security.UserDetailsImpl;
import org.example.bankcards.service.CardService;
import org.example.bankcards.service.UserService;
import org.example.bankcards.util.TestObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private CardService cardService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    private final UserDetailsImpl userDetails = TestObjectUtils.createUserDetailsImpl();
    private final UserResponseDTO userResponseDTO = TestObjectUtils.createUserResponseDTO();
    private final CardSimpleResponseDTO cardSimpleResponseDTO = TestObjectUtils.createCardSimpleResponseDTO();

    @Test
    void shouldGetUserProfileTest() throws Exception {
        // given

        // when
        when(userService.getUserById(userDetails.getId())).thenReturn(userResponseDTO);

        // then
        mockMvc.perform(get("/api/v1/users/profile")
                        .principal(() -> "user")
                        .requestAttr("userDetails", userDetails))
                .andExpect(status().isOk());
    }

    @Test
    void shouldGetUserCardsWithPaginationAndFiltersTest() throws Exception {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<CardSimpleResponseDTO> cardsPage = new PageImpl<>(List.of(cardSimpleResponseDTO));

        // when
        when(cardService.getUserCards(eq(userDetails.getId()), any(CardFilterRequestDTO.class), any(Pageable.class)))
                .thenReturn(cardsPage);

        // then
        mockMvc.perform(get("/api/v1/users")
                        .principal(() -> "user")
                        .param("status", "ACTIVE")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturnEmptyPageWhenNoCardsFoundTest() throws Exception {
        // given
        Page<CardSimpleResponseDTO> emptyPage = Page.empty();

        // when
        when(cardService.getUserCards(eq(userDetails.getId()), any(CardFilterRequestDTO.class), any(Pageable.class)))
                .thenReturn(emptyPage);

        // then
        mockMvc.perform(get("/api/v1/users")
                        .principal(() -> "user")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }
}
