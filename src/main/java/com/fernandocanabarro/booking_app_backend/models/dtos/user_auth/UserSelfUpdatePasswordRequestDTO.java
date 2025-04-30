package com.fernandocanabarro.booking_app_backend.models.dtos.user_auth;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.PasswordProperty;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSelfUpdatePasswordRequestDTO extends PasswordProperty {

    @NotBlank(message = "Required field")
    private String currentPassword;

}
