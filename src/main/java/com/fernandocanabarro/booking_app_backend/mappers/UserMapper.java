package com.fernandocanabarro.booking_app_backend.mappers;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;

import com.fernandocanabarro.booking_app_backend.models.dtos.user_auth.UserResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseUserProperties;
import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseUserPropertiesWithPassword;
import com.fernandocanabarro.booking_app_backend.models.entities.User;

public class UserMapper {

    public static User convertRequestToEntity(BaseUserPropertiesWithPassword request, PasswordEncoder passwordEncoder) {
        return User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .cpf(request.getCpf())
                .birthDate(request.getBirthDate())
                .createdAt(LocalDateTime.now())
                .activated(false)
                .build();
    }

    public static void updateUser(User entity, BaseUserProperties request) {
        entity.setFullName(request.getFullName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setCpf(request.getCpf());
        entity.setBirthDate(request.getBirthDate());
    }

    public static UserResponseDTO convertEntityToResponse(User entity) {
        return UserResponseDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .cpf(entity.getCpf())
                .birthDate(entity.getBirthDate())
                .createdAt(entity.getCreatedAt())
                .activated(entity.getActivated())
                .roles(entity.getRoles().stream().map(RoleMapper::convertEntityToResponse).toList())
                .workingHotelId(entity.getWorkingHotel() != null ? entity.getWorkingHotel().getId() : null)
                .build();
    }

}
