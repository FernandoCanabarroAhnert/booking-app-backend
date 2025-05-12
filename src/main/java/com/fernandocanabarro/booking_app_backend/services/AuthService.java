package com.fernandocanabarro.booking_app_backend.services;

import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.LoginRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.LoginResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.NewPasswordRequestoDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.PasswordRecoverRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.RegistrationRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserSelfUpdateInfosRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserSelfUpdatePasswordRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.ActivateAccountRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AlreadyExistsResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.User;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO request);
    void register(RegistrationRequestDTO request);
    void activateAccount(ActivateAccountRequestDTO request);
    User getConnectedUser();
    void validateJWTToken(String token);
    void userSelfUpdateInfos(UserSelfUpdateInfosRequestDTO request);
    void userSelfUpdatePassword(UserSelfUpdatePasswordRequestDTO request);
    void sendPasswordRecoverRequestEmail(PasswordRecoverRequestDTO request);
    void setNewPasswordFromPasswordRecoverRequest(NewPasswordRequestoDTO request);
    AlreadyExistsResponseDTO verifyIfUserExistsByEmail(String email);
    AlreadyExistsResponseDTO verifyIfUserExistsByCpf(String cpf);
    void verifyIfConnectedUserHasAdminPermission(Long id);
}
