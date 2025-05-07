package com.fernandocanabarro.booking_app_backend.models.dtos.booking;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseBookingRequestDTO;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateBookingRequestDTO extends BaseBookingRequestDTO {

    @NotNull(message = "Required field")
    private Long userId;
    private Boolean isFinished;

}
