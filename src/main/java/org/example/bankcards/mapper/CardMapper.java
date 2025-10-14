package org.example.bankcards.mapper;

import org.example.bankcards.dto.card.request.CardRequestDTO;
import org.example.bankcards.dto.card.response.CardResponseDTO;
import org.example.bankcards.dto.card.response.CardSimpleResponseDTO;
import org.example.bankcards.entity.CardEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CardMapper {
    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "maskedNumber", expression = "java(maskCardNumber(entity.getLast4()))")
    CardResponseDTO toResponseDTO(CardEntity entity);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "maskedNumber", expression = "java(maskCardNumber(entity.getLast4()))")
    CardSimpleResponseDTO toSimpleDTO(CardEntity entity);

    CardEntity toEntity(CardRequestDTO cardRequestDTO);

    default String maskCardNumber(String last4) {
        return "**** **** **** " + (last4 != null ? last4 : "----");
    }
}
