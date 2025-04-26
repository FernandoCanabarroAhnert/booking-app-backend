package com.fernandocanabarro.booking_app_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fernandocanabarro.booking_app_backend.models.dtos.LoginRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.LoginResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.NewPasswordRequestoDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.PasswordRecoverRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RegistrationRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserSelfUpdateInfosRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserSelfUpdatePasswordRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserWithPropertyAlreadyExistsDTO;
import com.fernandocanabarro.booking_app_backend.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegistrationRequestDTO request) {
        authService.register(request);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify-token")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> verifyToken(HttpServletRequest request) {
        authService.verifyToken(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-infos")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> userSelfUpdateInfos(@Valid @RequestBody UserSelfUpdateInfosRequestDTO request) {
        authService.userSelfUpdateInfos(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update-password")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> userSelfUpdatePassword(@Valid @RequestBody UserSelfUpdatePasswordRequestDTO request) {
        authService.userSelfUpdatePassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password-recover")
    public ResponseEntity<Void> sendPasswordRecoverEmail(@RequestBody PasswordRecoverRequestDTO request) {
        authService.sendPasswordRecoverEmail(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/new-password")
    public ResponseEntity<Void> setNewPasswordFromPasswordRecover(@RequestBody @Valid NewPasswordRequestoDTO request) {
        authService.setNewPasswordFromPasswordRecover(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify-email")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<UserWithPropertyAlreadyExistsDTO> verifyEmail(@RequestParam(name = "email") String email) {
        return ResponseEntity.ok(this.authService.verifyIfUserExistsByEmail(email));
    }

    @GetMapping("/verify-cpf")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<UserWithPropertyAlreadyExistsDTO> verifyCpf(@RequestParam(name = "cpf") String cpf) {
        return ResponseEntity.ok(this.authService.verifyIfUserExistsByCpf(cpf));
    }

}
