package com.fernandocanabarro.booking_app_backend.models.dtos.base;

import java.math.BigDecimal;

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
    private String description;
    private BigDecimal averageRating;
    private Integer roomQuantity;
    private String street;
    private String number;
    private String city;
    private String zipCode;
    private String state;
    private String phone;

}
