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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fernandocanabarro.booking_app_backend.models.dtos.UserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserWithPropertyAlreadyExistsDTO;
import com.fernandocanabarro.booking_app_backend.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> findAll(Pageable pageable) {
        return ResponseEntity.ok(this.userService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(this.userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody UserRequestDTO request) {
        this.userService.create(request);
        return ResponseEntity.status(201).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @Valid @RequestBody UserRequestDTO request) {
        this.userService.update(id, request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify-email")
    public ResponseEntity<UserWithPropertyAlreadyExistsDTO> verifyEmail(@RequestParam(name = "email") String email) {
        return ResponseEntity.ok(this.userService.verifyIfUserExistsByEmail(email));
    }

    @GetMapping("/verify-cpf")
    public ResponseEntity<UserWithPropertyAlreadyExistsDTO> verifyCpf(@RequestParam(name = "cpf") String cpf) {
        return ResponseEntity.ok(this.userService.verifyIfUserExistsByCpf(cpf));
    }

}
