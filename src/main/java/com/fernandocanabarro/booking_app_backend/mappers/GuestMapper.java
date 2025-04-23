package com.fernandocanabarro.booking_app_backend.mappers;

import java.time.LocalDateTime;

import com.fernandocanabarro.booking_app_backend.models.dtos.GuestRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.GuestResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Guest;

public class GuestMapper {

    public static Guest convertRequestToEntity(GuestRequestDTO request) {
        return Guest.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .cpf(request.getCpf())
                .birthDate(request.getBirthDate())
                .createdAt(LocalDateTime.now())
                .build();
    }

    public static void updateEntity(Guest entity, GuestRequestDTO request) {
        entity.setFullName(request.getFullName());
        entity.setEmail(request.getEmail());
        entity.setPhone(request.getPhone());
        entity.setCpf(request.getCpf());
        entity.setBirthDate(request.getBirthDate());
    }

    public static GuestResponseDTO convertEntityToResponse(Guest entity) {
        return GuestResponseDTO.builder()
                .id(entity.getId())
                .fullName(entity.getFullName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .cpf(entity.getCpf())
                .birthDate(entity.getBirthDate())
                .createdAt(entity.getCreatedAt())
                .build();
    }

}
