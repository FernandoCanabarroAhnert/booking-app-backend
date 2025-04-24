package com.fernandocanabarro.booking_app_backend.models.dtos;

import jakarta.validation.constraints.NotBlank;
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
