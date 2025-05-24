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

import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.LoginRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.LoginResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.NewPasswordRequestoDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.PasswordRecoverRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.RegistrationRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserSelfUpdateInfosRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserSelfUpdatePasswordRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.ActivateAccountRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AlreadyExistsResponseDTO;
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
    public ResponseEntity<LoginResponseDTO> guestLogin(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request, false));
    }

    @PostMapping("/login/admin")
    public ResponseEntity<LoginResponseDTO> adminLogin(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request, true));
    }

    @PutMapping("/activate-account")
    public ResponseEntity<Void> activateAccount(@RequestBody ActivateAccountRequestDTO request) {
        this.authService.activateAccount(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/token/validate")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> validateJWTToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization").split(" ")[1];
        authService.validateJWTToken(token);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> userSelfUpdateInfos(@Valid @RequestBody UserSelfUpdateInfosRequestDTO request) {
        authService.userSelfUpdateInfos(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile/password")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<Void> userSelfUpdatePassword(@Valid @RequestBody UserSelfUpdatePasswordRequestDTO request) {
        authService.userSelfUpdatePassword(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(@RequestBody PasswordRecoverRequestDTO request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody @Valid NewPasswordRequestoDTO request) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('ROLE_GUEST','ROLE_OPERATOR','ROLE_ADMIN')")
    public ResponseEntity<UserResponseDTO> getUserSelfInfos() {
        return ResponseEntity.ok(this.authService.getMe());
    }

    @GetMapping("/verify-email")
    public ResponseEntity<AlreadyExistsResponseDTO> verifyEmail(@RequestParam(name = "email") String email) {
        return ResponseEntity.ok(this.authService.verifyIfUserExistsByEmail(email));
    }

    @GetMapping("/verify-cpf")
    public ResponseEntity<AlreadyExistsResponseDTO> verifyCpf(@RequestParam(name = "cpf") String cpf) {
        return ResponseEntity.ok(this.authService.verifyIfUserExistsByCpf(cpf));
    }

}
