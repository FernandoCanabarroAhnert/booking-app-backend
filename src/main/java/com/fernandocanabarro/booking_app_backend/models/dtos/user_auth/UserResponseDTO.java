package com.fernandocanabarro.booking_app_backend.models.dtos.user_auth;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
public class UserResponseDTO {

    private Long id;
    private String fullName;
    private String email;
    private String phone;
    private String cpf;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    private List<RoleResponseDTO> roles;
    private Long workingHotelId;

}
