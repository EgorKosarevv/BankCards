package org.example.bankcards.dto.card.transfer.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransferResponseDTO {
    private Long id;
    private Long fromCardId;
    private Long toCardId;
    private BigDecimal amount;
    private LocalDateTime createdAt;
}
