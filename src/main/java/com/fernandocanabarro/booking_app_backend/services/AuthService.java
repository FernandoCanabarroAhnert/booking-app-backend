package com.fernandocanabarro.booking_app_backend.services;

import com.fernandocanabarro.booking_app_backend.models.dtos.LoginRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.LoginResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RegistrationRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserSelfUpdateInfosRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserSelfUpdatePasswordRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserWithPropertyAlreadyExistsDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.User;

import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);
    void register(RegistrationRequestDTO request);
    User getConnectedUser();
    void verifyToken(HttpServletRequest request);
    void userSelfUpdateInfos(UserSelfUpdateInfosRequestDTO request);
    void userSelfUpdatePassword(UserSelfUpdatePasswordRequestDTO request);
    UserWithPropertyAlreadyExistsDTO verifyIfUserExistsByEmail(String email);
    UserWithPropertyAlreadyExistsDTO verifyIfUserExistsByCpf(String cpf);
}
