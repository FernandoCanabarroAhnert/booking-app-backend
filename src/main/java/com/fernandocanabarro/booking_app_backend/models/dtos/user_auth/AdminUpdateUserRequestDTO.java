package com.fernandocanabarro.booking_app_backend.models.dtos.user_auth;

import java.util.List;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseUserProperties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminUpdateUserRequestDTO extends BaseUserProperties {

    @NotNull(message = "Required field")
    private Boolean activated;
    @NotEmpty(message = "Required field")
    private List<Long> rolesIds;

}
