package com.fernandocanabarro.booking_app_backend.models.dtos;

import java.time.LocalDate;

import org.hibernate.validator.constraints.br.CPF;

import jakarta.validation.constraints.Email;
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
public class GuestRequestDTO {

    @NotBlank(message = "Required field")
    private String fullName;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Required field")
    private String email;
    @NotBlank(message = "Required field")
    private String phone;
    @NotBlank(message = "Required field")
    @CPF(message = "Invalid CPF format")
    private String cpf;
    @NotNull(message = "Required field")
    private LocalDate birthDate;

}
