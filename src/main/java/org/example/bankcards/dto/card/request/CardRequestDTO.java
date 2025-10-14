package org.example.bankcards.dto.card.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CardRequestDTO {
    @NotNull(message = "Owner ID must not be null")
    private Long ownerId;
}
