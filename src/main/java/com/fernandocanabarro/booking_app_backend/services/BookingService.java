package com.fernandocanabarro.booking_app_backend.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingPaymentRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingResponseDTO;

public interface BookingService {

    List<BookingDetailResponseDTO> findAllBookingsDetailed();
    Page<BookingResponseDTO> findAllPageable(Pageable pageable);
    BookingDetailResponseDTO findById(Long id);
    void createBooking(BookingRequestDTO request, boolean isSelfBooking);
    void updateBooking(Long id, BaseBookingRequestDTO request, boolean isSelfBooking);
    void updateBookingPayment(Long id, BookingPaymentRequestDTO request, boolean isSelfBooking);
    void deleteBooking(Long id);

    Page<BookingResponseDTO> findAllBookingsByUser(Long userId, Pageable pageable, boolean isSelfUser);
    Page<BookingResponseDTO> findAllBookingsByRoom(Long roomId, Pageable pageable);

}
