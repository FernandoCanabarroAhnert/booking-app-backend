package com.fernandocanabarro.booking_app_backend.factories;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fernandocanabarro.booking_app_backend.models.entities.Booking;

public class BookingFactory {

    public static Booking createBooking() {
        return Booking.builder()
            .id(1L)
            .checkIn(LocalDate.of(2025, 7, 1))
            .checkOut(LocalDate.of(2025, 7, 7))
            .room(RoomFactory.createRoom())
            .user(UserFactory.createUser())
            .createdAt(LocalDateTime.now())
            .isFinished(false)
            .payment(PaymentFactory.createDinheiroPayment())
            .build();
    }

}
