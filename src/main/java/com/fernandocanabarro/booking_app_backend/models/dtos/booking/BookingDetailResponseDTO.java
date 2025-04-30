package com.fernandocanabarro.booking_app_backend.models.dtos.booking;

import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseBookingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookingDetailResponseDTO extends BaseBookingResponseDTO {

    private UserResponseDTO user;
    private RoomResponseDTO room;
    private BookingPaymentResponseDTO payment;

}
