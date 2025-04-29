package com.fernandocanabarro.booking_app_backend.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fernandocanabarro.booking_app_backend.models.dtos.BookingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RoomDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RoomRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.services.BookingService;
import com.fernandocanabarro.booking_app_backend.services.RoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService RoomService;
    private final BookingService bookingService;

    @GetMapping
    public ResponseEntity<Page<RoomResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(this.RoomService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDetailResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(this.RoomService.findById(id));
    }

    @GetMapping("/{id}/unavailable-days")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<List<LocalDate>> getUnavailableDatesFromRoomByRoomId(@PathVariable Long id) {
        return ResponseEntity.ok(this.RoomService.getUnavailableDatesFromRoomByRoomId(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestPart("request") RoomRequestDTO request,
                                       @RequestPart(value = "images") List<MultipartFile> images) {                   
        this.RoomService.create(request, images);
        return ResponseEntity.status(201).build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                    @Valid @RequestPart("request") RoomRequestDTO request,
                                    @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        this.RoomService.update(id, request, images);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.RoomService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/bookings")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Page<BookingResponseDTO>> findAllBookingsByRoom(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(this.bookingService.findAllBookingsByRoom(id, pageable));
    }
}
