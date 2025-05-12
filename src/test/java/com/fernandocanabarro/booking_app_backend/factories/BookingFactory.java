package com.fernandocanabarro.booking_app_backend.factories;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.AdminBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.AdminUpdateBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Booking;

public class BookingFactory {

    public static Booking createBooking() {
        return Booking.builder()
            .id(1L)
            .checkIn(LocalDate.of(2025, 7, 1))
            .checkOut(LocalDate.of(2025, 7, 7))
            .room(RoomFactory.createRoom())
            .user(UserFactory.createUser())
            .guestsQuantity(1)
            .createdAt(LocalDateTime.now())
            .isFinished(false)
            .payment(PaymentFactory.createDinheiroPayment())
            .build();
    }

    public static BookingRequestDTO createBookingRequest() {
        BookingRequestDTO request = new BookingRequestDTO();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.of(2025, 7, 1));
        request.setCheckOut(LocalDate.of(2025, 7, 7));
        request.setGuestsQuantity(1);
        request.setPayment(PaymentFactory.createDinheiroPaymentRequest());
        return request;
    }

    public static AdminBookingRequestDTO createAdminBookingRequest() {
        AdminBookingRequestDTO request = new AdminBookingRequestDTO();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.of(2025, 7, 1));
        request.setCheckOut(LocalDate.of(2025, 7, 7));
        request.setGuestsQuantity(1);
        request.setPayment(PaymentFactory.createDinheiroPaymentRequest());
        request.setUserId(1L);
        return request;
    }

    public static BaseBookingRequestDTO createUpdateBookingRequest() {
        BaseBookingRequestDTO request = new BaseBookingRequestDTO();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.of(2025, 7, 1));
        request.setCheckOut(LocalDate.of(2025, 7, 7));
        request.setGuestsQuantity(1);
        return request;
    }

    public static AdminUpdateBookingRequestDTO createAdminUpdateBookingRequest() {
        AdminUpdateBookingRequestDTO request = new AdminUpdateBookingRequestDTO();
        request.setRoomId(1L);
        request.setCheckIn(LocalDate.of(2025, 7, 1));
        request.setCheckOut(LocalDate.of(2025, 7, 7));
        request.setGuestsQuantity(1);
        request.setUserId(1L);
        request.setIsFinished(true);
        return request;
    }

}
