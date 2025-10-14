package org.example.bankcards.mapper;

import org.example.bankcards.dto.card.transfer.request.CardTransferRequestDTO;
import org.example.bankcards.dto.card.transfer.response.TransferResponseDTO;
import org.example.bankcards.entity.TransferEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TransferMapper {

    @Mapping(target = "fromCard", ignore = true)
    @Mapping(target = "toCard", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    TransferEntity toEntity(CardTransferRequestDTO dto);

    @Mapping(source = "fromCard.id", target = "fromCardId")
    @Mapping(source = "toCard.id", target = "toCardId")
    TransferResponseDTO toResponseDTO(TransferEntity entity);
}
