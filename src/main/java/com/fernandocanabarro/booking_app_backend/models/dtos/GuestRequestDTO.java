package com.fernandocanabarro.booking_app_backend.models.dtos;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GuestRequestDTO {

    private String fullName;
    private String email;
    private String phone;
    private String cpf;
    private LocalDate birthDate;

}
