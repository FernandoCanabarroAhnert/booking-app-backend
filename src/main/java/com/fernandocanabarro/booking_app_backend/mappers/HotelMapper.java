package com.fernandocanabarro.booking_app_backend.mappers;

import java.util.ArrayList;
import java.util.Arrays;

import com.fernandocanabarro.booking_app_backend.models.dtos.HotelRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.HotelResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;

public class HotelMapper {

    public static Hotel convertRequestToEntity(HotelRequestDTO request) {
        return Hotel.builder()
            .name(request.getName())
            .roomQuantity(request.getRoomQuantity())
            .address(request.getAddress())
            .city(request.getCity())
            .zipCode(request.getZipCode())
            .state(request.getState())
            .phone(request.getPhone())
            .rooms(new ArrayList<>(Arrays.asList()))
            .build();
    }

    public static void updateEntity(Hotel hotel, HotelRequestDTO request) {
        hotel.setName(request.getName());
        hotel.setRoomQuantity(request.getRoomQuantity());
        hotel.setAddress(request.getAddress());
        hotel.setCity(request.getCity());
        hotel.setZipCode(request.getZipCode());
        hotel.setState(request.getState());
        hotel.setPhone(request.getPhone());
    }

    public static HotelResponseDTO convertEntityToResponse(Hotel entity) {
        return HotelResponseDTO.builder()
            .id(entity.getId())
            .name(entity.getName())
            .roomQuantity(entity.getRoomQuantity())
            .address(entity.getAddress())
            .city(entity.getCity())
            .zipCode(entity.getZipCode())
            .state(entity.getState())
            .phone(entity.getPhone())
            .build();
    }

}
