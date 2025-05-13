package com.fernandocanabarro.booking_app_backend.models.dtos.room;

import java.math.BigDecimal;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomRatingRequestDTO {

    @NotNull(message = "Required field")
    @Min(1)
    @Max(5)
    private BigDecimal rating;
    private String description;

}
