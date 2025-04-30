package com.fernandocanabarro.booking_app_backend.models.dtos.booking;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseBookingResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO extends BaseBookingResponseDTO {

    private Long userId;
    private Long roomId;

}
