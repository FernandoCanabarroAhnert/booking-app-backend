package com.fernandocanabarro.booking_app_backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fernandocanabarro.booking_app_backend.models.dtos.RoomRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RoomResponseDTO;

public interface RoomService {

    Page<RoomResponseDTO> findAll(Pageable pageable);
    RoomResponseDTO findById(Long id);
    void create(RoomRequestDTO request);
    void update(Long id, RoomRequestDTO request);
    void delete(Long id);

}
