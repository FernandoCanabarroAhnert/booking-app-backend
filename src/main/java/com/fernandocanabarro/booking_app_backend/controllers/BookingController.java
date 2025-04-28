package com.fernandocanabarro.booking_app_backend.controllers;

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

import com.fernandocanabarro.booking_app_backend.models.dtos.AdminBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.BookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.BookingResponseDTO;
import com.fernandocanabarro.booking_app_backend.services.BookingService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Page<BookingResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(this.bookingService.findAll(pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<BookingResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(this.bookingService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> adminCreate(@Valid @RequestBody AdminBookingRequestDTO request) {
        this.bookingService.create(request, false);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/self")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody BookingRequestDTO request) {
        this.bookingService.create(request, true);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> adminUpdate(@PathVariable Long id, @Valid @RequestBody AdminBookingRequestDTO request) {
        this.bookingService.update(id, request, false);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/self")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody BookingRequestDTO request) {
        this.bookingService.update(id, request, true);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.bookingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-bookings")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Page<BookingResponseDTO>> findMyBookings(Pageable pageable) {
        return ResponseEntity.ok(this.bookingService.findAllBookingsByUser(null, pageable, true));
    }

}
