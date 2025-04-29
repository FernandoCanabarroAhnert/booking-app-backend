package com.fernandocanabarro.booking_app_backend.controllers;

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

import com.fernandocanabarro.booking_app_backend.models.dtos.HotelDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.HotelRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.HotelResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.services.HotelService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<Page<HotelResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(this.hotelService.findAll(pageable));
    }

    @GetMapping("/{id}/rooms")
    public ResponseEntity<Page<RoomResponseDTO>> findRoomsByHotelId(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(this.hotelService.findRoomsByHotelId(id, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelDetailResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(this.hotelService.findById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestPart("request") HotelRequestDTO request,
                                    @RequestPart("images") List<MultipartFile> images) {
        this.hotelService.create(request, images);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Long id, 
                                    @Valid @RequestPart("request") HotelRequestDTO request,
                                    @RequestPart(name = "images", required = false) List<MultipartFile> images) {
        this.hotelService.update(id, request, images);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.hotelService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
