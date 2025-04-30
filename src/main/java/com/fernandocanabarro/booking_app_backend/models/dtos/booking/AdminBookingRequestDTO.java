package com.fernandocanabarro.booking_app_backend.models.dtos.booking;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminBookingRequestDTO extends BookingRequestDTO {

    @NotNull(message = "Required field")
    private Long userId;
}
