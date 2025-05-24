package com.fernandocanabarro.booking_app_backend.models.dtos.hotel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelSearchResponseDTO {

    private Long id;
    private String name;

}
