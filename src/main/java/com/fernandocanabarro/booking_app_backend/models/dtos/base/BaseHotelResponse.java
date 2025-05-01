package com.fernandocanabarro.booking_app_backend.models.dtos.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseHotelResponse {

    private Long id;
    private String name;
    private Integer roomQuantity;
    private String street;
    private String number;
    private String city;
    private String zipCode;
    private String state;
    private String phone;

}
