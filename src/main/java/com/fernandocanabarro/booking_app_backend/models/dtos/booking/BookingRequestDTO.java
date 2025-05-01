package com.fernandocanabarro.booking_app_backend.models.dtos.booking;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {

    @NotNull(message = "Required field")
    private Long roomId;
    @NotNull(message = "Required field")
    @FutureOrPresent(message = "Check-in date must be today or in the future")
    private LocalDate checkIn;
    @Future(message = "Check-out date must be in the future")
    @NotNull(message = "Required field")
    private LocalDate checkOut;
    @Valid
    @NotNull(message = "Required field")
    private BookingPaymentRequestDTO payment;
}
