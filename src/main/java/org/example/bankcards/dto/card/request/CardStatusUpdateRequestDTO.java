package org.example.bankcards.dto.card.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bankcards.entity.CardStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardStatusUpdateRequestDTO {
    @NotNull(message = "Status must not be null")
    private CardStatus status;
}
