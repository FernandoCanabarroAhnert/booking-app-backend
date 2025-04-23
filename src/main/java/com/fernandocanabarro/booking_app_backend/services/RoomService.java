package com.fernandocanabarro.booking_app_backend.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fernandocanabarro.booking_app_backend.models.dtos.RoomRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RoomResponseDTO;

public interface RoomService {

    Page<RoomResponseDTO> findAll(Pageable pageable);
    RoomResponseDTO findById(Long id);
    List<LocalDate> getUnavailableDatesFromRoomByRoomId(Long id);
    void create(RoomRequestDTO request);
    void update(Long id, RoomRequestDTO request);
    void delete(Long id);

}
