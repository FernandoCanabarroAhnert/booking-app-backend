package com.fernandocanabarro.booking_app_backend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelResponseDTO {

    private Long id;
    private String name;
    private Integer roomQuantity;
    private String address;
    private String city;
    private String zipCode;
    private String state;
    private String phone;

}
