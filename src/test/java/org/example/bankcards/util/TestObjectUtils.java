package org.example.bankcards.util;

import org.example.bankcards.dto.card.request.CardDepositRequestDTO;
import org.example.bankcards.dto.card.request.CardFilterRequestDTO;
import org.example.bankcards.dto.card.request.CardRequestDTO;
import org.example.bankcards.dto.card.request.CardStatusUpdateRequestDTO;
import org.example.bankcards.dto.card.response.CardResponseDTO;
import org.example.bankcards.dto.card.response.CardSimpleResponseDTO;
import org.example.bankcards.dto.card.transfer.request.CardTransferRequestDTO;
import org.example.bankcards.dto.card.transfer.response.TransferResponseDTO;
import org.example.bankcards.dto.user.request.RefreshTokenRequestDTO;
import org.example.bankcards.dto.user.request.UserAuthRequestDTO;
import org.example.bankcards.dto.user.request.UserRegisterRequestDTO;
import org.example.bankcards.dto.user.request.UserRoleUpdateRequestDTO;
import org.example.bankcards.dto.user.request.UserStatusUpdateRequestDTO;
import org.example.bankcards.dto.user.response.UserAuthResponseDTO;
import org.example.bankcards.dto.user.response.UserResponseDTO;
import org.example.bankcards.entity.CardEntity;
import org.example.bankcards.entity.RefreshTokenEntity;
import org.example.bankcards.entity.RoleEntity;
import org.example.bankcards.entity.TransferEntity;
import org.example.bankcards.entity.UserEntity;
import org.example.bankcards.entity.UserRole;
import org.example.bankcards.security.UserDetailsImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class TestObjectUtils {

    //    role
    public static RoleEntity createRoleEntity() {
        return RoleEntity.builder()
                .id(1L)
                .roleName(UserRole.ADMIN)
                .build();
    }

    //  auth
    public static UserRegisterRequestDTO createRegisterRequest() {
        return UserRegisterRequestDTO.builder()
                .build();
    }

    public static UserAuthRequestDTO createLoginRequest() {
        return UserAuthRequestDTO.builder()
                .build();
    }

    public static UserAuthResponseDTO createLoginResponseDTO() {
        return UserAuthResponseDTO.builder()
                .build();
    }

    public static RefreshTokenRequestDTO createRefreshToken() {
        return RefreshTokenRequestDTO.builder()
                .build();
    }

    public static RefreshTokenEntity createRefreshTokenEntity() {
        return RefreshTokenEntity.builder()
                .build();
    }


    public static RefreshTokenEntity createRefreshToken(UserEntity user) {
        return RefreshTokenEntity.builder()
                .user(user)
                .expiryDate((Instant.now().plus(1, ChronoUnit.DAYS)))
                .build();
    }

    //  user
    public static UserEntity createUserEntity() {
        return UserEntity.builder()
                .id(1L)
                .build();
    }

    public static UserResponseDTO createUserResponseDTO() {
        return UserResponseDTO.builder()
                .build();
    }



    public static UserStatusUpdateRequestDTO createUserStatusUpdateRequestDTO() {
        return UserStatusUpdateRequestDTO.builder()
                .build();
    }

    public static UserRoleUpdateRequestDTO createUserRoleUpdateRequestDTO() {
        return UserRoleUpdateRequestDTO.builder()
                .build();
    }

//    card
    public static CardEntity createCardEntity() {
        return CardEntity.builder()
                .id(1L)
                .owner(createUserEntity())
                .build();
    }
    public static CardResponseDTO createCardResponseDTO() {
        return CardResponseDTO.builder()
                .build();
    }

    public static CardRequestDTO createCardRequestDTO() {
        return CardRequestDTO.builder()
                .ownerId(1L)
                .build();
    }

    public static CardFilterRequestDTO createCardFilterRequestDTO() {
        return CardFilterRequestDTO.builder()
                .build();
    }

    public static CardDepositRequestDTO createCardDepositRequestDTO() {
        return CardDepositRequestDTO.builder()
                .cardId(1L)
                .build();
    }

    public static CardSimpleResponseDTO createCardSimpleResponseDTO() {
        return CardSimpleResponseDTO.builder().build();
    }

    public static CardStatusUpdateRequestDTO createCardStatusUpdateRequestDTO() {
        return CardStatusUpdateRequestDTO.builder().build();
    }


//    transfer
    public static TransferEntity createTransferEntity() {
        return TransferEntity.builder()
                .id(1L)
                .build();
    }

    public static TransferResponseDTO createTransferResponseDTO() {
        return TransferResponseDTO.builder().build();
    }

    public static CardTransferRequestDTO createCardTransferRequestDTO() {
        return CardTransferRequestDTO.builder().build();
    }

    //    userDetails
    public static UserDetailsImpl createUserDetailsImpl() {
        UserDetailsImpl userDetails = UserDetailsImpl.builder()
                .userEntity(createUserEntity())
                .build();

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userDetails, null));
        return userDetails;
    }



}
