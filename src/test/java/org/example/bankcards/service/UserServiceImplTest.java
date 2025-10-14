package org.example.bankcards.service;

import org.example.bankcards.dto.user.request.UserFilterRequestDTO;
import org.example.bankcards.dto.user.request.UserStatusUpdateRequestDTO;
import org.example.bankcards.dto.user.response.UserResponseDTO;
import org.example.bankcards.entity.RoleEntity;
import org.example.bankcards.entity.UserEntity;
import org.example.bankcards.entity.UserRole;
import org.example.bankcards.exception.NotFoundException;
import org.example.bankcards.mapper.UserMapper;
import org.example.bankcards.repository.UserRepository;
import org.example.bankcards.service.impl.UserServiceImpl;
import org.example.bankcards.util.TestObjectUtils;
import org.hibernate.HibernateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleService roleService;

    @InjectMocks
    private UserServiceImpl userService;

    private final UserEntity userEntity = TestObjectUtils.createUserEntity();
    private final UserResponseDTO userResponseDTO = TestObjectUtils.createUserResponseDTO();
    private final UserStatusUpdateRequestDTO userStatusUpdateRequestDTO = TestObjectUtils.createUserStatusUpdateRequestDTO();
    private final Set<String> roleNames = Set.of("USER");
    private final RoleEntity roleEntity = TestObjectUtils.createRoleEntity();

    @Test
    void shouldGetUserEntityByIdTest() {
        // given


        // when
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        UserEntity result = userService.getUserEntityById(1);

        // then
        assertNotNull(result);
        assertEquals(userEntity, result);
        verify(userRepository).findById(1L);
    }

    @Test
    void shouldNotGetUserEntityByIdWhenUserNotFoundTest() {
        // given
        String expected = "not found";

        // when
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserEntityById(1));

        // then
        assertTrue(exception.getMessage().contains(expected));
    }

    @Test
    void shouldGetUserDTOByIdTest() {
        // given


        // when
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userMapper.toResponseDTO(userEntity)).thenReturn(userResponseDTO);
        UserResponseDTO result = userService.getUserById(1);

        // then
        assertNotNull(result);
        assertEquals(userResponseDTO, result);
        verify(userRepository).findById(1L);
        verify(userMapper).toResponseDTO(userEntity);
    }

    @Test
    void shouldNotGetUserDTOByIdWhenUserNotFoundTest() {
        // given
        String expected = "not found";

        // when
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserById(1));

        // then
        assertTrue(exception.getMessage().contains(expected));
    }


    @Test
    void shouldGetUserByPhoneTest() {
        // given

        // when
        when(userRepository.findByPhone("user")).thenReturn(Optional.of(userEntity));
        UserEntity result = userService.getUserEntityByPhone("user");

        // then
        assertNotNull(result);
        assertEquals(userEntity, result);
        verify(userRepository).findByPhone("user");
    }

    @Test
    void shouldNotGetUserByPhoneWhenNotFoundTest() {
        // given
        String expected = "not found";

        // when
        when(userRepository.findByPhone("user")).thenReturn(Optional.empty());
        NotFoundException exception = assertThrows(NotFoundException.class, () -> userService.getUserEntityByPhone("user"));

        // then
        assertTrue(exception.getMessage().contains(expected));
        verify(userRepository).findByPhone("user");
    }


    @Test
    void shouldUpdateUserStatusTest() {
        // given
        userEntity.setEnabled(false);

        // when
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        userService.updateUserStatus(1, userStatusUpdateRequestDTO, 2);

        // then
        verify(userRepository).save(userEntity);
    }

    @Test
    void shouldUpdateUserRolesTest() {
        // given
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(roleService.getRoleByName(UserRole.USER)).thenReturn(roleEntity);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        // when
        userService.updateUserRoles(1, roleNames, 2);

        // then
        verify(userRepository).save(userEntity);
        assertTrue(userEntity.getRoles().contains(roleEntity));
    }

    @Test
    void shouldNotUpdateUserRolesWhenHibernateExceptionTest() {
        // given
        String expected = "DB error";

        // when
        doThrow(new HibernateException("DB error")).when(userRepository).save(any());
        when(userRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        HibernateException exception = assertThrows(HibernateException.class, () -> userService.updateUserRoles(1, roleNames, 2));

        // then
        assertTrue(exception.getMessage().contains(expected));
    }


    @Test
    void shouldReturnEmptyPageWhenNoUsersFound() {
        // given
        UserFilterRequestDTO filter = new UserFilterRequestDTO();
        Pageable pageable = PageRequest.of(0, 10);
        Page<UserEntity> emptyPage = Page.empty(pageable);

        when(userRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(emptyPage);

        // when
        Page<UserResponseDTO> result = userService.getAllUsers(filter, pageable);

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll(any(Specification.class), eq(pageable));
        verifyNoInteractions(userMapper);
    }

}
