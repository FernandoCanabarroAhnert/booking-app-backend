package com.fernandocanabarro.booking_app_backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fernandocanabarro.booking_app_backend.models.dtos.HotelRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.HotelResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RoomResponseDTO;

public interface HotelService {

    Page<HotelResponseDTO> findAll(Pageable pageable);
    Page<RoomResponseDTO> findRoomsByHotelId(Long hotelId, Pageable pageable);
    HotelResponseDTO findById(Long id);
    void create(HotelRequestDTO request);
    void update(Long id, HotelRequestDTO request);
    void delete(Long id);

}
