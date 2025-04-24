package com.fernandocanabarro.booking_app_backend.models.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelRequestDTO {

    @NotBlank(message = "Required field")
    private String name;
    @NotNull(message = "Required field")
    private Integer roomQuantity;
    @NotBlank(message = "Required field")
    private String address;
    @NotBlank(message = "Required field")
    private String city;
    @NotBlank(message = "Required field")
    private String zipCode;
    @NotBlank(message = "Required field")
    private String state;
    @NotBlank(message = "Required field")
    private String phone;

}
