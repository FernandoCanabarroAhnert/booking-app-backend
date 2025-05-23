package com.fernandocanabarro.booking_app_backend.mappers;

import java.util.ArrayList;
import java.util.Arrays;

import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;

public class HotelMapper {

    public static Hotel convertRequestToEntity(HotelRequestDTO request) {
        return Hotel.builder()
            .name(request.getName())
            .description(request.getDescription())
            .roomQuantity(request.getRoomQuantity())
            .street(request.getStreet())
            .number(request.getNumber())
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
        hotel.setDescription(request.getDescription());
        hotel.setRoomQuantity(request.getRoomQuantity());
        hotel.setStreet(request.getStreet());
        hotel.setNumber(request.getNumber());
        hotel.setCity(request.getCity());
        hotel.setZipCode(request.getZipCode());
        hotel.setState(request.getState());
        hotel.setPhone(request.getPhone());
    }

    public static HotelResponseDTO convertEntityToResponse(Hotel entity) {
        HotelResponseDTO response = new HotelResponseDTO();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setAverageRating(entity.getAverageRating());
        response.setRoomQuantity(entity.getRoomQuantity());
        response.setStreet(entity.getStreet());
        response.setNumber(entity.getNumber());
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
        response.setDescription(entity.getDescription());
        response.setAverageRating(entity.getAverageRating());
        response.setRoomQuantity(entity.getRoomQuantity());
        response.setStreet(entity.getStreet());
        response.setNumber(entity.getNumber());
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
