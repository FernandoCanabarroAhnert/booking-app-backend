package com.fernandocanabarro.booking_app_backend.controllers;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fernandocanabarro.booking_app_backend.models.dtos.GuestRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.GuestResponseDTO;
import com.fernandocanabarro.booking_app_backend.services.GuestService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/guests")
@RequiredArgsConstructor
public class GuestController {

    private final GuestService guestService;

    @GetMapping
    public ResponseEntity<Page<GuestResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(this.guestService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuestResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(this.guestService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody GuestRequestDTO request) {
        this.guestService.create(request);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody GuestRequestDTO request) {
        this.guestService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.guestService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
