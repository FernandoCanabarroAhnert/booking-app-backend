package com.fernandocanabarro.booking_app_backend.models.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {

    private Long guestId;
    private Long roomId;
    private LocalDate checkIn;
    private LocalDate checkOut;

}
