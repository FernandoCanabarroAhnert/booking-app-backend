package com.fernandocanabarro.booking_app_backend.factories;

import com.fernandocanabarro.booking_app_backend.models.entities.Role;

public class RoleFactory {

    public static Role createGuestRole() {
        return new Role(1L,"ROLE_GUEST");
    }
    public static Role createOperatorRole() {
        return new Role(2L,"ROLE_OPERATOR");
    }
    public static Role createAdminRole() {
        return new Role(3L,"ROLE_ADMIN");
    }

}
