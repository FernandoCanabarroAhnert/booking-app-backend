package com.fernandocanabarro.booking_app_backend.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRatingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRatingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.services.BookingService;
import com.fernandocanabarro.booking_app_backend.services.RoomService;
import com.fernandocanabarro.booking_app_backend.services.excel.RoomsExcelExporter;
import com.fernandocanabarro.booking_app_backend.services.jasper.JasperService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final BookingService bookingService;
    private final JasperService jasperService;

    @GetMapping
    public ResponseEntity<Page<RoomResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(this.roomService.findAllPageable(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomDetailResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(this.roomService.findById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestPart("request") RoomRequestDTO request,
                                       @RequestPart(value = "images") List<MultipartFile> images) {                   
        this.roomService.create(request, images);
        return ResponseEntity.status(201).build();
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Long id,
                                    @Valid @RequestPart("request") RoomRequestDTO request,
                                    @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        this.roomService.update(id, request, images);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.roomService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/bookings")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Page<BookingResponseDTO>> findAllBookingsByRoom(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(this.bookingService.findAllBookingsByRoom(id, pageable));
    }

    @GetMapping("/pdf")
    public void exportToPdf(HttpServletResponse response) {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        String fileName = "rooms_" + currentDateTime + ".pdf";
        String headerValue = "inline; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        jasperService.exportToPdf(response, JasperService.ROOMS);
    }

    @GetMapping("/pdf/group-by-hotel")
    public void exportToPdfGroupByHotel(HttpServletResponse response) {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        String fileName = "rooms-group-by-hotel_" + currentDateTime + ".pdf";
        String headerValue = "inline; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        jasperService.exportToPdf(response, JasperService.ROOMS_GROUP_BY_HOTEL);
    }

    @GetMapping("/excel")
    public void exportToExcel(HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        String fileName = "rooms_" + currentDateTime + ".xlsx";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        List<RoomResponseDTO> rooms = roomService.findAll();
        RoomsExcelExporter roomsExcelExporter = new RoomsExcelExporter(rooms);
        roomsExcelExporter.export(response);
    }

    @GetMapping("/{id}/ratings")
    public ResponseEntity<Page<RoomRatingResponseDTO>> findAllRatingsByRoomId(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(this.roomService.findAllRatingsByRoomId(id, pageable));
    }

    @PostMapping("/{id}/ratings")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> addRating(@PathVariable Long id, @Valid @RequestBody RoomRatingRequestDTO request) {
        this.roomService.addRating(id, request);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/ratings/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> updateRating(@PathVariable Long id,
                                             @Valid @RequestBody RoomRatingRequestDTO request) {
        this.roomService.updateRating(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/ratings/{id}")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        this.roomService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }

}
