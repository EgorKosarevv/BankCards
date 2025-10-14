package org.example.bankcards.mapper;

import org.example.bankcards.dto.user.request.UserRegisterRequestDTO;
import org.example.bankcards.dto.user.response.UserResponseDTO;
import org.example.bankcards.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

    UserResponseDTO toResponseDTO(UserEntity entity);

    UserEntity toEntity(UserRegisterRequestDTO userRegisterRequestDTO);
}