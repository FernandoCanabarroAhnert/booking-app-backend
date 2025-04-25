package com.fernandocanabarro.booking_app_backend.services;

import com.fernandocanabarro.booking_app_backend.models.dtos.LoginRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.LoginResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RegistrationRequestDTO;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);
    void register(RegistrationRequestDTO request);
    void verifyToken(HttpServletRequest request);
}
