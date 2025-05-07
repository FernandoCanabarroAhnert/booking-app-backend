package com.fernandocanabarro.booking_app_backend.models.dtos.booking;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseBookingRequestDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO extends BaseBookingRequestDTO {

    @Valid
    @NotNull(message = "Required field")
    private BookingPaymentRequestDTO payment;
}
