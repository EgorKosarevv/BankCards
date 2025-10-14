package org.example.bankcards.dto.card.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bankcards.entity.CardStatus;
import lombok.Builder;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardSimpleResponseDTO {
    private Long id;
    private Long ownerId;
    private String maskedNumber;
    private CardStatus status;
    private BigDecimal balance;
}
