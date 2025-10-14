package org.example.bankcards.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bankcards.dto.card.request.CardDepositRequestDTO;
import org.example.bankcards.security.UserDetailsImpl;
import org.example.bankcards.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
@Tag(name = "Card", description = "Operations on cards")
public class CardController {
    private final CardService cardService;

    @Operation(
            summary = "Request card block",
            description = "Allows the user to request blocking their own active card."
    )
    @PutMapping("/{cardId}/block-request")
    public ResponseEntity<Void> requestCardBlock(
            @PathVariable Long cardId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        cardService.requestBlock(userDetails.getId(), cardId);
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "Deposit funds to a card")
    @PutMapping("/deposit")
    public ResponseEntity<Void> depositToCard(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody @Valid CardDepositRequestDTO request
    ) {
        cardService.depositToCard(request, user.getId());
        return ResponseEntity.ok().build();
    }
}
