package com.fernandocanabarro.booking_app_backend.mappers;

import java.util.ArrayList;
import java.util.Arrays;

import com.fernandocanabarro.booking_app_backend.models.dtos.HotelDetailResponseDTO;
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
            .workers(new ArrayList<>(Arrays.asList()))
            .images(new ArrayList<>(Arrays.asList()))
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
        HotelResponseDTO response = new HotelResponseDTO();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setRoomQuantity(entity.getRoomQuantity());
        response.setAddress(entity.getAddress());
        response.setCity(entity.getCity());
        response.setZipCode(entity.getZipCode());
        response.setState(entity.getState());
        response.setPhone(entity.getPhone());
        response.setCardDisplayImage(ImageMapper.convertEntityResponseDTO(entity.getImages().get(0)));
        return response;
    }

    public static HotelDetailResponseDTO convertEntityToDetailResponse(Hotel entity) {
        HotelDetailResponseDTO response = new HotelDetailResponseDTO();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setRoomQuantity(entity.getRoomQuantity());
        response.setAddress(entity.getAddress());
        response.setCity(entity.getCity());
        response.setZipCode(entity.getZipCode());
        response.setState(entity.getState());
        response.setPhone(entity.getPhone());
        response.setImages(entity.getImages().stream()
            .map(ImageMapper::convertEntityResponseDTO)
            .toList());
        return response;
    }

}
