package com.fernandocanabarro.booking_app_backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fernandocanabarro.booking_app_backend.models.dtos.GuestRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.GuestResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.UserWithPropertyAlreadyExistsDTO;

public interface GuestService {

    Page<GuestResponseDTO> findAll(Pageable pageable);
    GuestResponseDTO findById(Long id);
    void create(GuestRequestDTO request);
    void update(Long id, GuestRequestDTO request);
    void delete(Long id);

    UserWithPropertyAlreadyExistsDTO verifyIfGuestExistsByEmail(String email);
    UserWithPropertyAlreadyExistsDTO verifyIfGuestExistsByCpf(String cpf);

}
