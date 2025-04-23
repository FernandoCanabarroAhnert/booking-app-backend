package com.fernandocanabarro.booking_app_backend.models.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class GuestResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String cpf;
    private LocalDate birthDate;
    private LocalDateTime createdAt;

}
