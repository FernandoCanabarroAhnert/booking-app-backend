package com.fernandocanabarro.booking_app_backend.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRatingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRatingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomResponseDTO;

public interface RoomService {

    List<RoomResponseDTO> findAll();
    Page<RoomResponseDTO> findAllPageable(List<String> types, Integer capacity, BigDecimal minPrice, BigDecimal maxPrice, 
        String city, LocalDate checkIn, LocalDate checkOut, Long hotelId, Pageable pageable);
    Page<RoomResponseDTO> findAllPageable(Pageable pageable);
    RoomDetailResponseDTO findById(Long id);
    void create(RoomRequestDTO request, List<MultipartFile> images);
    void update(Long id, RoomRequestDTO request, List<MultipartFile> images);
    void delete(Long id);
    void deleteImage(Long imageId);
    void addRating(Long roomRatingId, RoomRatingRequestDTO request);
    void updateRating(Long roomRatingId, RoomRatingRequestDTO request);
    void deleteRating(Long roomRatingId);
    Page<RoomRatingResponseDTO> findAllRatingsByRoomId(Long roomId, Pageable pageable);

}
