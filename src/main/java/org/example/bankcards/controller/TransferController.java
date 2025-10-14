package org.example.bankcards.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.bankcards.dto.card.transfer.request.CardTransferRequestDTO;
import org.example.bankcards.dto.card.transfer.response.TransferResponseDTO;
import org.example.bankcards.security.UserDetailsImpl;
import org.example.bankcards.service.TransferService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/v1/transfers")
@RequiredArgsConstructor
@Tag(name = "Transfers", description = "Operations on transfers between cards")
public class TransferController {

    private final TransferService transferService;

    @Operation(
            summary = "Transfer funds between user's own cards",
            description = "Allows a user to transfer money between their own active cards"
    )
    @PostMapping
    public ResponseEntity<Void> transferBetweenOwnCards(
            @AuthenticationPrincipal UserDetailsImpl user,
            @RequestBody @Valid CardTransferRequestDTO request
    ) {
        transferService.transferBetweenOwnCards(user.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/card/{cardId}")
    public ResponseEntity<Page<TransferResponseDTO>> getCardTransfers(
            @PathVariable Long cardId,
            @ParameterObject Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl user
    ) {
        Page<TransferResponseDTO> transfers = transferService.getTransfersForCard(cardId, pageable, user.getId());
        return ResponseEntity.ok(transfers);
    }
}
