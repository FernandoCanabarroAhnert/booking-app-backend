package com.fernandocanabarro.booking_app_backend.factories;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.RegistrationRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserSelfUpdateInfosRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserSelfUpdatePasswordRequestDTO;
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

    public static RegistrationRequestDTO createRegistrationRequestDTO() {
        RegistrationRequestDTO request = new RegistrationRequestDTO();
        request.setFullName("name");
        request.setEmail("email");
        request.setPassword("password");
        request.setPhone("(11) 99999-9999");
        request.setCpf("cpf");
        request.setBirthDate(LocalDate.of(2005, 10, 28));
        return request;
    }

    public static UserSelfUpdateInfosRequestDTO createUserSelfUpdateInfosRequestDTO() {
        UserSelfUpdateInfosRequestDTO request = new UserSelfUpdateInfosRequestDTO();
        request.setFullName("name");
        request.setEmail("email");
        request.setPhone("(11) 99999-9999");
        request.setCpf("cpf");
        request.setBirthDate(LocalDate.of(2005, 10, 28));
        return request;
    }

    public static UserSelfUpdatePasswordRequestDTO createUserSelfUpdatePasswordRequestDTO() {
        UserSelfUpdatePasswordRequestDTO request = new UserSelfUpdatePasswordRequestDTO();
        request.setCurrentPassword("password");
        request.setPassword("newPassword");
        return request;
    }
}
