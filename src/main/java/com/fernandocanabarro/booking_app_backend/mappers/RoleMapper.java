package com.fernandocanabarro.booking_app_backend.mappers;

import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.RoleResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Role;

public class RoleMapper {

    public static RoleResponseDTO convertEntityToResponse(Role entity) {
        return RoleResponseDTO.builder()
            .id(entity.getId())
            .authority(entity.getAuthority())
            .build();
    }

}
