package org.example.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankcards.dto.user.request.RefreshTokenRequestDTO;
import org.example.bankcards.dto.user.request.UserAuthRequestDTO;
import org.example.bankcards.dto.user.request.UserRegisterRequestDTO;
import org.example.bankcards.dto.user.response.UserAuthResponseDTO;
import org.example.bankcards.security.JwtAuthFilter;
import org.example.bankcards.service.AuthService;
import org.example.bankcards.util.TestObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    private final UserRegisterRequestDTO userRegisterRequestDTO = TestObjectUtils.createRegisterRequest();
    private final UserAuthRequestDTO userAuthRequestDTO = TestObjectUtils.createLoginRequest();
    private final UserAuthResponseDTO userAuthResponseDTO = TestObjectUtils.createLoginResponseDTO();
    private final RefreshTokenRequestDTO refreshTokenRequestDTO = TestObjectUtils.createRefreshToken();

    @Test
    void shouldRegisterUserTest() throws Exception {
        // given
        userRegisterRequestDTO.setPhone("12345678900");
        userRegisterRequestDTO.setPassword("password");
        userRegisterRequestDTO.setFullName("John Doe");



        // when
        doNothing().when(authService).registerUser(userRegisterRequestDTO);

        // then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterRequestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldNotRegisterUserWhenBadRequestTest() throws Exception {
        // given

        // when
        doNothing().when(authService).registerUser(userRegisterRequestDTO);

        // then
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRegisterRequestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldLoginUserTest() throws Exception {
        // given
        userRegisterRequestDTO.setPhone("12345678900");
        userAuthRequestDTO.setPassword("password");


        // when
        when(authService.login(userAuthRequestDTO)).thenReturn(userAuthResponseDTO);


        // then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAuthRequestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotLoginUserWhenBadRequestTest() throws Exception {
        // given

        // when
        when(authService.login(userAuthRequestDTO)).thenReturn(userAuthResponseDTO);

        // then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userAuthRequestDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldRefreshTokenTest() throws Exception {
        // given
        refreshTokenRequestDTO.setRefreshToken("refreshToken");


        // when
        when(authService.refreshToken(refreshTokenRequestDTO)).thenReturn(userAuthResponseDTO);


        // then
        mockMvc.perform(post("/api/v1/auth/updateToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotRefreshTokenWhenBadRequestTest() throws Exception {
        // given

        // when
        when(authService.refreshToken(refreshTokenRequestDTO)).thenReturn(userAuthResponseDTO);

        // then
        mockMvc.perform(post("/api/v1/auth/updateToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequestDTO)))
                .andExpect(status().isBadRequest());
    }


}
