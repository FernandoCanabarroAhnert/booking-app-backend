package com.fernandocanabarro.booking_app_backend.mappers;

import java.time.LocalDateTime;

import com.fernandocanabarro.booking_app_backend.models.dtos.BookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.BookingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Booking;
import com.fernandocanabarro.booking_app_backend.models.entities.Guest;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;

public class BookingMapper {

    public static Booking convertRequestToEntity(BookingRequestDTO request, Room room, Guest guest) {
        return Booking.builder()
                .room(room)
                .guest(guest)
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .createdAt(LocalDateTime.now())
                .isFinished(false)
                .build();
    }

    public static void updateEntity(Booking entity, BookingRequestDTO request) {
        entity.setCheckIn(request.getCheckIn());
        entity.setCheckOut(request.getCheckOut());
    }

    public static BookingResponseDTO convertEntityToResponse(Booking entity) {
        return BookingResponseDTO.builder()
                .id(entity.getId())
                .guest(GuestMapper.convertEntityToResponse(entity.getGuest()))
                .room(RoomMapper.convertEntityToResponse(entity.getRoom()))
                .checkIn(entity.getCheckIn())
                .checkOut(entity.getCheckOut())
                .createdAt(entity.getCreatedAt())
                .isFinished(entity.isFinished())
                .totalPrice(entity.getTotalPrice())
                .build();
    }

}
