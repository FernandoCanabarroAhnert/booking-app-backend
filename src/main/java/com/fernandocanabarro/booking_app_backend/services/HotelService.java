package com.fernandocanabarro.booking_app_backend.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.fernandocanabarro.booking_app_backend.models.dtos.HotelDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.HotelRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.HotelResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RoomResponseDTO;

public interface HotelService {

    Page<HotelResponseDTO> findAll(Pageable pageable);
    Page<RoomResponseDTO> findRoomsByHotelId(Long hotelId, Pageable pageable);
    HotelDetailResponseDTO findById(Long id);
    void create(HotelRequestDTO request, List<MultipartFile> images);
    void update(Long id, HotelRequestDTO request, List<MultipartFile> images);
    void delete(Long id);

}
