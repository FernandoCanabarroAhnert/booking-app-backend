package com.fernandocanabarro.booking_app_backend.mappers;

import java.time.LocalDateTime;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseBookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingPaymentResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.booking.BookingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Booking;
import com.fernandocanabarro.booking_app_backend.models.entities.CartaoPayment;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;

public class BookingMapper {

    public static Booking convertRequestToEntity(BookingRequestDTO request, Room room, User user) {
        return Booking.builder()
                .room(room)
                .user(user)
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .createdAt(LocalDateTime.now())
                .isFinished(false)
                .build();
    }

    public static void updateEntity(Booking entity, BaseBookingRequestDTO request) {
        entity.setCheckIn(request.getCheckIn());
        entity.setCheckOut(request.getCheckOut());
        entity.getPayment().setAmount(entity.getTotalPrice());
    }

    public static BookingResponseDTO convertEntityToResponse(Booking entity) {
        BookingResponseDTO response = new BookingResponseDTO();
        response.setId(entity.getId());
        response.setCheckIn(entity.getCheckIn());
        response.setCheckOut(entity.getCheckOut());
        response.setCreatedAt(entity.getCreatedAt());
        response.setFinished(entity.isFinished());
        response.setTotalPrice(entity.getTotalPrice());
        response.setUserId(entity.getUser().getId());
        response.setRoomId(entity.getRoom().getId());
        return response;
    }

    public static BookingDetailResponseDTO convertEntityToDetailResponse(Booking entity) {
        BookingDetailResponseDTO response = new BookingDetailResponseDTO();
        response.setId(entity.getId());
        response.setCheckIn(entity.getCheckIn());
        response.setCheckOut(entity.getCheckOut());
        response.setCreatedAt(entity.getCreatedAt());
        response.setFinished(entity.isFinished());
        response.setTotalPrice(entity.getTotalPrice());
        response.setUser(UserMapper.convertEntityToResponse(entity.getUser()));
        response.setRoom(RoomMapper.convertEntityToResponse(entity.getRoom()));
        response.setPayment(new BookingPaymentResponseDTO(
            entity.getPayment().getPaymentType().getPaymentType(),
            entity.getPayment().isOnlinePayment(),
            entity.getPayment() instanceof CartaoPayment
                    ? ((CartaoPayment) entity.getPayment()).getInstallmentQuantity()
                    : null
            )
        );
        return response;
    }

}
