package com.fernandocanabarro.booking_app_backend.models.dtos.base;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BaseBookingRequestDTO {

    @NotNull(message = "Required field")
    private Long roomId;
    @NotNull(message = "Required field")
    private LocalDate checkIn;
    @NotNull(message = "Required field")
    private LocalDate checkOut;
    @NotNull(message = "Required field")
    private Integer guestsQuantity;

}
