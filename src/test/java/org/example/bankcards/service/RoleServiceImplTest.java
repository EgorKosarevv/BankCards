package org.example.bankcards.service;

import org.example.bankcards.entity.RoleEntity;
import org.example.bankcards.entity.UserRole;
import org.example.bankcards.exception.NotFoundException;
import org.example.bankcards.repository.RoleRepository;
import org.example.bankcards.service.impl.RoleServiceImpl;
import org.example.bankcards.util.TestObjectUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private final UserRole roleName = UserRole.ADMIN;
    private final RoleEntity roleEntity = TestObjectUtils.createRoleEntity();

    @Test
    void shouldGetRoleByNameTest() {
        // given

        // when
        when(roleRepository.findByRoleName(roleName)).thenReturn(Optional.of(roleEntity));
        RoleEntity result = roleService.getRoleByName(roleName);

        // then
        assertNotNull(result);
        assertEquals(roleEntity, result);
        verify(roleRepository).findByRoleName(roleName);
    }

    @Test
    void shouldNotGetRoleWhenNotFoundTest() {
        // given
        String expected = "not found";

        // when
        when(roleRepository.findByRoleName(roleName)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> roleService.getRoleByName(roleName));

        // then
        assertTrue(exception.getMessage().contains(expected));
        verify(roleRepository).findByRoleName(roleName);
    }

}
