package com.fernandocanabarro.booking_app_backend.services;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    Page<BookingResponseDTO> findAllPageable(Pageable pageable, LocalDate checkIn, LocalDate checkOut, Long hotelId, 
        BigDecimal minPrice, BigDecimal maxPrice, List<String> paymentType);
    BookingDetailResponseDTO findById(Long id, boolean verifyPermission);
    void createBooking(BookingRequestDTO request, boolean isSelfBooking);
    void updateBooking(Long id, BaseBookingRequestDTO request, boolean isSelfBooking);
    void updateBookingPayment(Long id, BookingPaymentRequestDTO request, boolean isSelfBooking);
    void deleteBooking(Long id);

    Page<BookingResponseDTO> findAllBookingsByUser(Long userId, Pageable pageable, boolean isSelfUser);
    Page<BookingResponseDTO> findAllBookingsByRoom(Long roomId, Pageable pageable);

}
