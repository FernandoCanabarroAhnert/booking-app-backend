package com.fernandocanabarro.booking_app_backend.models.dtos.user_auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchResponseDTO {

    private Long id;
    private String cpf;

}
