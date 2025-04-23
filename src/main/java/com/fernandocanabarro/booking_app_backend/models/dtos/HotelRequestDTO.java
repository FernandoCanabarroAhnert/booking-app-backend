package com.fernandocanabarro.booking_app_backend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelRequestDTO {

    private String name;
    private String address;
    private String city;
    private String zipCode;
    private String state;
    private String phone;

}
