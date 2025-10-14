package org.example.bankcards.dto.card.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bankcards.entity.CardStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponseDTO {
    private Long id;
    private Long ownerId;
    private String maskedNumber;
    private BigDecimal balance;
    private CardStatus status;
    private LocalDateTime expiry;
    private boolean blockRequested;
}
