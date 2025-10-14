package org.example.bankcards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.bankcards.dto.user.request.UserRoleUpdateRequestDTO;
import org.example.bankcards.dto.user.request.UserStatusUpdateRequestDTO;
import org.example.bankcards.security.JwtAuthFilter;
import org.example.bankcards.security.UserDetailsImpl;
import org.example.bankcards.service.UserService;
import org.example.bankcards.util.TestObjectUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminUserController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AdminUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    private final UserDetailsImpl userDetails = TestObjectUtils.createUserDetailsImpl();
    private final UserRoleUpdateRequestDTO userRoleUpdateRequestDTO = TestObjectUtils.createUserRoleUpdateRequestDTO();
    private final UserStatusUpdateRequestDTO updateUserStatusRequestDTO = TestObjectUtils.createUserStatusUpdateRequestDTO();



    @Test
    void shouldUpdateRolesAdminUserTest() throws Exception {
        // given
        userRoleUpdateRequestDTO.setRoles(Set.of("ADMIN", "USER"));

        // when
        doNothing().when(userService).updateUserRoles(1, userRoleUpdateRequestDTO.getRoles(), 2);

        // then
        mockMvc.perform(put("/api/v1/admin/users/{userId}/roles", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRoleUpdateRequestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldNotUpdateRolesAdminUserWhenBadRequestTest() throws Exception {
        // given

        // when
        doNothing().when(userService).updateUserRoles(1, userRoleUpdateRequestDTO.getRoles(), 2);

        // then
        mockMvc.perform(put("/api/v1/admin/users/{userId}/roles", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRoleUpdateRequestDTO)))
                .andExpect(status().isBadRequest());
    }


    @Test
    void shouldUpdateStatusAdminUserTest() throws Exception {
        // given
        updateUserStatusRequestDTO.setEnable(true);

        // when
        doNothing().when(userService).updateUserStatus(1, updateUserStatusRequestDTO, 2);

        // then
        mockMvc.perform(put("/api/v1/admin/users/{userId}/status", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserStatusRequestDTO)))
                .andExpect(status().isOk());
    }


}
