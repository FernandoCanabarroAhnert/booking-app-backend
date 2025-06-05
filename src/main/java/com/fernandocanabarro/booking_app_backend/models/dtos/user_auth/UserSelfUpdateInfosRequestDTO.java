package com.fernandocanabarro.booking_app_backend.models.dtos.user_auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSelfUpdateInfosRequestDTO {

    @NotBlank(message = "Required field")
    private String fullName;
    @Email(message = "Invalid email format")
    @NotBlank(message = "Required field")
    private String email; 
    @NotBlank(message = "Required field")
    private String phone;

}
