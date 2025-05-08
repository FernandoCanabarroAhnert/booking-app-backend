package com.fernandocanabarro.booking_app_backend.factories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.fernandocanabarro.booking_app_backend.models.entities.User;

public class UserFactory {

    public static User createUser() {
        return User.builder()
                .id(1L)
                .fullName("name")
                .email("email")
                .password("password")
                .phone("(11) 99999-9999")
                .cpf("cpf")
                .birthDate(LocalDate.of(2005, 10, 28))
                .createdAt(LocalDateTime.now())
                .roles(Arrays.asList(RoleFactory.createGuestRole()).stream().collect(Collectors.toSet()))
                .build();
    }

}
