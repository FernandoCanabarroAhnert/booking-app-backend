package com.fernandocanabarro.booking_app_backend.models.dtos;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.PasswordProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NewPasswordRequestoDTO extends PasswordProperty {

    private String code;

}
