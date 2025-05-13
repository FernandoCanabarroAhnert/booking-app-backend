package com.fernandocanabarro.booking_app_backend.mappers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomDetailResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRatingRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRatingResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.room.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;
import com.fernandocanabarro.booking_app_backend.models.entities.RoomRating;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.models.enums.RoomTypeEnum;

public class RoomMapper {

    public static Room convertRequestToEntity(RoomRequestDTO request, Hotel hotel) {
        return Room.builder()
            .number(request.getNumber())
            .floor(request.getFloor())
            .type(RoomTypeEnum.fromValue(request.getType()))
            .pricePerNight(request.getPricePerNight())
            .description(request.getDescription())
            .capacity(request.getCapacity())
            .bookings(new ArrayList<>(Arrays.asList()))
            .ratings(new ArrayList<>(Arrays.asList()))
            .hotel(hotel)
            .images(new ArrayList<>(Arrays.asList()))
            .build();
    }

    public static void updateRoom(Room entity, RoomRequestDTO request) {
        entity.setNumber(request.getNumber());
        entity.setFloor(request.getFloor());
        entity.setType(RoomTypeEnum.fromValue(request.getType()));
        entity.setPricePerNight(request.getPricePerNight());
        entity.setDescription(request.getDescription());
        entity.setCapacity(request.getCapacity());
    }

    public static RoomResponseDTO convertEntityToResponse(Room entity) {
        RoomResponseDTO response = new RoomResponseDTO();
        response.setId(entity.getId());
        response.setNumber(entity.getNumber());
        response.setFloor(entity.getFloor());
        response.setType(entity.getType().getRoomType());
        response.setPricePerNight(entity.getPricePerNight());
        response.setDescription(entity.getDescription());
        response.setCapacity(entity.getCapacity());
        response.setHotelId(entity.getHotel().getId());
        response.setAverageRating(entity.getAverageRating());
        response.setHotelName(entity.getHotel().getName());
        response.setCardDisplayImage(ImageMapper.convertEntityResponseDTO(entity.getImages().get(0)));
        return response;
    }

    public static RoomDetailResponseDTO convertEntityToDetailResponse(Room entity) {
        RoomDetailResponseDTO response = new RoomDetailResponseDTO();
        response.setId(entity.getId());
        response.setNumber(entity.getNumber());
        response.setFloor(entity.getFloor());
        response.setType(entity.getType().getRoomType());
        response.setPricePerNight(entity.getPricePerNight());
        response.setDescription(entity.getDescription());
        response.setCapacity(entity.getCapacity());
        response.setAverageRating(entity.getAverageRating());
        response.setHotel(HotelMapper.convertEntityToResponse(entity.getHotel()));
        response.setUnavailableDates(entity.getUnavailableDates());
        response.setImages(entity.getImages().stream()
                .map(ImageMapper::convertEntityResponseDTO)
                .toList());
        return response;
    }

    public static RoomRating convertRoomRatingRequestToEntity(RoomRatingRequestDTO request, Room room, User user) {
        return RoomRating.builder()
            .rating(request.getRating())
            .description(request.getDescription())
            .room(room)
            .user(user)
            .createdAt(LocalDateTime.now())
            .build();
    }

    public static void updateRoomRating(RoomRating roomRating, RoomRatingRequestDTO request) {
        roomRating.setRating(request.getRating());
        roomRating.setDescription(request.getDescription());
    }

    public static RoomRatingResponseDTO convertRoomRatingEntityToResponse(RoomRating roomRating) {
        RoomRatingResponseDTO response = new RoomRatingResponseDTO();
        response.setId(roomRating.getId());
        response.setRoomId(roomRating.getRoom().getId());
        response.setUserFullName(roomRating.getUser().getFullName());
        response.setUserEmail(roomRating.getUser().getEmail());
        response.setRating(roomRating.getRating());
        response.setDescription(roomRating.getDescription());
        response.setCreatedAt(roomRating.getCreatedAt());
        return response;
    }


}
