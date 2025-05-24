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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelSearchResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.services.HotelService;
import com.fernandocanabarro.booking_app_backend.services.excel.HotelExcelExporter;
import com.fernandocanabarro.booking_app_backend.services.jasper.JasperService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;
    private final JasperService jasperService;

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<List<HotelSearchResponseDTO>> findAllByName(@RequestParam(name = "name") String name) {
        return ResponseEntity.ok(this.hotelService.findAllByName(name));
    }

    @GetMapping
    public ResponseEntity<Page<HotelResponseDTO>> findAll(Pageable pageable, @RequestParam(defaultValue = "", name = "name") String name) {
        return ResponseEntity.ok(this.hotelService.findAllPageable(pageable, name));
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

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @DeleteMapping("/{id}/images")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        this.hotelService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void exportToPdf(HttpServletResponse response) {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        String fileName = "hotels_" + currentDateTime + ".pdf";
        String headerValue = "inline; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        jasperService.exportToPdf(response, JasperService.HOTELS);
    }

    @GetMapping("/excel")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void exportToExcel(HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        String fileName = "hotels_" + currentDateTime + ".xlsx";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        List<HotelResponseDTO> hotels = hotelService.findAll();
        HotelExcelExporter hotelExcelExporter = new HotelExcelExporter(hotels);
        hotelExcelExporter.export(response);
    }

}
