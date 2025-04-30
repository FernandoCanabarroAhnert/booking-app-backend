package com.fernandocanabarro.booking_app_backend.models.dtos.room;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequestDTO {

    @NotBlank(message = "Required field")
    private String number;
    @NotNull(message = "Required field")
    private Integer floor;
    @NotNull(message = "Required field")
    private Integer type;
    @Positive(message = "Price must be positive")
    @NotNull(message = "Required field")
    private BigDecimal pricePerNight;
    @NotBlank(message = "Required field")
    private String description;
    @NotNull(message = "Required field")
    private Integer capacity;
    @NotNull(message = "Required field")
    private Long hotelId;

}
