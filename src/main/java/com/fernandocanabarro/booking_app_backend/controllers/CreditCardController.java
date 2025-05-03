package com.fernandocanabarro.booking_app_backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fernandocanabarro.booking_app_backend.models.dtos.credit_card.CreditCardRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.credit_card.CreditCardResponseDTO;
import com.fernandocanabarro.booking_app_backend.services.CreditCardService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/credit-cards")
@RequiredArgsConstructor
public class CreditCardController {

    private final CreditCardService creditCardService;

    @GetMapping("/my-credit-cards")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Page<CreditCardResponseDTO>> getConnectedUserCreditCards(Pageable pageable) {
        return ResponseEntity.ok(this.creditCardService.getConnectedUserCreditCards(pageable));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> addCreditCard(@Valid @RequestBody CreditCardRequestDTO request) {
        this.creditCardService.addCreditCard(request);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> deleteCreditCard(@PathVariable Long id) {
        this.creditCardService.deleteCreditCard(id);
        return ResponseEntity.noContent().build();
    }
}
