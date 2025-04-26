package com.fernandocanabarro.booking_app_backend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fernandocanabarro.booking_app_backend.models.dtos.RoomRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.services.RoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService RoomService;

    @GetMapping
    public ResponseEntity<Page<RoomResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(this.RoomService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(this.RoomService.findById(id));
    }

    @GetMapping("/{id}/unavailable-days")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<List<LocalDate>> getUnavailableDatesFromRoomByRoomId(@PathVariable Long id) {
        return ResponseEntity.ok(this.RoomService.getUnavailableDatesFromRoomByRoomId(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody RoomRequestDTO request) {
        this.RoomService.create(request);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody RoomRequestDTO request) {
        this.RoomService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.RoomService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
