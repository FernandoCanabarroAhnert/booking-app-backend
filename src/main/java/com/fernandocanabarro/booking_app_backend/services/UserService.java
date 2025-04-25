package com.fernandocanabarro.booking_app_backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fernandocanabarro.booking_app_backend.models.dtos.UserRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserWithPropertyAlreadyExistsDTO;

public interface UserService {

    Page<UserResponseDTO> findAll(Pageable pageable);
    UserResponseDTO findById(Long id);
    void create(UserRequestDTO request);
    void update(Long id, UserRequestDTO request);
    void delete(Long id);

    UserWithPropertyAlreadyExistsDTO verifyIfUserExistsByEmail(String email);
    UserWithPropertyAlreadyExistsDTO verifyIfUserExistsByCpf(String cpf);

}
