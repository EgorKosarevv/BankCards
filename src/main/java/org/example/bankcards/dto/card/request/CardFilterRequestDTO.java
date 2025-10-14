package org.example.bankcards.dto.card.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.bankcards.entity.CardStatus;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardFilterRequestDTO {
    private Long ownerId;

    @Pattern(regexp = "^[0-9]{4}$", message = "Last4 must be exactly 4 digits")
    private String last4;

    private CardStatus status;

    private Boolean blockRequested;

    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    private BigDecimal minBalance;

    @DecimalMin(value = "0.00", message = "Balance cannot be negative")
    private BigDecimal maxBalance;
}
