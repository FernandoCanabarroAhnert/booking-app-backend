package com.fernandocanabarro.booking_app_backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AdminCreateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AdminUpdateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserResponseDTO;

public interface UserService {

    Page<UserResponseDTO> adminFindAllUsers(Pageable pageable);
    UserResponseDTO adminFindUserById(Long id);
    void adminCreateUser(AdminCreateUserRequestDTO request);
    void adminUpdateUser(Long id, AdminUpdateUserRequestDTO request);
    void adminDeleteUser(Long id);

    

}
