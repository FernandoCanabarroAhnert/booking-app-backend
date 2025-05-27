package com.fernandocanabarro.booking_app_backend.controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AdminCreateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AdminUpdateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserSearchResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingResponseDTO;
import com.fernandocanabarro.booking_app_backend.services.BookingService;
import com.fernandocanabarro.booking_app_backend.services.UserService;
import com.fernandocanabarro.booking_app_backend.services.excel.UsersExcelExporter;
import com.fernandocanabarro.booking_app_backend.services.jasper.JasperService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final BookingService bookingService;
    private final JasperService jasperService;

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<List<UserSearchResponseDTO>> findAllByCpf(@RequestParam(name = "cpf") String cpf) {
        return ResponseEntity.ok(this.userService.findAllByCpf(cpf));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<Page<UserResponseDTO>> findAll(Pageable pageable, @RequestParam(name = "fullName", defaultValue = "") String fullName) {
        return ResponseEntity.ok(this.userService.adminFindAllUsersPageable(pageable, fullName));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(this.userService.adminFindUserById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<Void> create(@Valid @RequestBody AdminCreateUserRequestDTO request) {
        this.userService.adminCreateUser(request);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody AdminUpdateUserRequestDTO request) {
        this.userService.adminUpdateUser(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.userService.adminDeleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/bookings")
    @PreAuthorize("hasAnyRole('ROLE_OPERATOR', 'ROLE_ADMIN')")
    public Page<BookingResponseDTO> findUserBookings(@PathVariable Long id, Pageable pageable) {
        return this.bookingService.findAllBookingsByUser(id, pageable, false);
    }

    @GetMapping("/pdf")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void exportToPdf(HttpServletResponse response) {
        response.setContentType("application/pdf");
        String headerKey = "Content-Disposition";
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        String fileName = "users_" + currentDateTime + ".pdf";
        String headerValue = "inline; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        jasperService.exportToPdf(response, JasperService.USERS);
    }

    @GetMapping("/excel")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public void exportToExcel(HttpServletResponse response) {
        response.setContentType("application/octet-stream");
        String headerKey = "Content-Disposition";
        String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy_HH:mm:ss"));
        String fileName = "users_" + currentDateTime + ".xlsx";
        String headerValue = "attachment; filename=" + fileName;
        response.setHeader(headerKey, headerValue);
        List<UserResponseDTO> users = this.userService.adminFindAllUsers();
        UsersExcelExporter usersExcelExporter = new UsersExcelExporter(users);
        usersExcelExporter.export(response);
    }

}
