package com.fernandocanabarro.booking_app_backend.models.dtos.base;

import com.fernandocanabarro.booking_app_backend.validators.annotations.PasswordValid;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@PasswordValid
public abstract class PasswordProperty implements PasswordPropertyInterface {

    @NotBlank(message = "Required field")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    private String password;

}
