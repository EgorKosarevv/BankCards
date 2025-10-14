package org.example.bankcards.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bankcards.dto.card.request.CardFilterRequestDTO;
import org.example.bankcards.dto.card.request.CardRequestDTO;
import org.example.bankcards.dto.card.request.CardStatusUpdateRequestDTO;
import org.example.bankcards.dto.card.response.CardSimpleResponseDTO;
import org.example.bankcards.service.CardService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/cards")
@RequiredArgsConstructor
@Tag(name = "AdminCard", description = "Operations on cards only for admin")
public class AdminCardController {
    private final CardService cardService;

    @Operation(summary = "Create a new card", description = "Create a new card for the authenticated user.")
    @PostMapping
    public ResponseEntity<Void> createCard(
            @RequestBody @Valid CardRequestDTO request
    ) {
        cardService.createCard(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    @Operation(summary = "Get all cards with filtering and pagination")
    public ResponseEntity<Page<CardSimpleResponseDTO>> getAllCards(
            @Valid @ParameterObject CardFilterRequestDTO filter,
            @ParameterObject Pageable pageable
    ) {
        Page<CardSimpleResponseDTO> cards = cardService.getAllCards(filter, pageable);
        return ResponseEntity.ok(cards);
    }

    @Operation(
            summary = "Change card status",
            description = "Change the status of a card (only ACTIVE and BLOCKED allowed)."
    )
    @PutMapping("/{cardId}/status")
    public ResponseEntity<Void> changeCardStatus(
            @PathVariable Long cardId,
            @RequestBody @Valid CardStatusUpdateRequestDTO request
    ) {
        cardService.changeCardStatus(cardId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Delete a card")
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long cardId) {
        cardService.deleteCard(cardId);
        return ResponseEntity.noContent().build();
    }


}
