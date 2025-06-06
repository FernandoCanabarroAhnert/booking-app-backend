package com.fernandocanabarro.booking_app_backend.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AdminCreateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.AdminUpdateUserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserSearchResponseDTO;

public interface UserService {

    List<UserSearchResponseDTO> findAllByCpf(String cpf);
    List<UserResponseDTO> adminFindAllUsers();
    Page<UserResponseDTO> adminFindAllUsersPageable(Pageable pageable, String fullName);
    UserResponseDTO adminFindUserById(Long id);
    void adminCreateUser(AdminCreateUserRequestDTO request);
    void adminUpdateUser(Long id, AdminUpdateUserRequestDTO request);
    void adminDeleteUser(Long id);

    

}
