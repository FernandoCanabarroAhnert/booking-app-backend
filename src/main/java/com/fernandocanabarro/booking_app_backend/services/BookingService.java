package com.fernandocanabarro.booking_app_backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fernandocanabarro.booking_app_backend.models.dtos.BookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.BookingResponseDTO;

public interface BookingService {

    Page<BookingResponseDTO> findAll(Pageable pageable);
    BookingResponseDTO findById(Long id);
    void create(BookingRequestDTO request);
    void update(Long id, BookingRequestDTO request);
    void delete(Long id);

}
