package com.fernandocanabarro.booking_app_backend.models.dtos;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestDTO extends RegistrationRequestDTO {

    @NotEmpty(message = "Required field")
    private List<Long> rolesIds;

    private Long workingHotelId;

}
