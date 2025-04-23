package com.fernandocanabarro.booking_app_backend.models.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequestDTO {

    private String number;
    private Integer floor;
    private Integer type;
    private BigDecimal pricePerNight;
    private String description;
    private Integer capacity;
    private Long hotelId;

}
