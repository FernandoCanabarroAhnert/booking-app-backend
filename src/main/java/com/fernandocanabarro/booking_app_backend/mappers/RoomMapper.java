package com.fernandocanabarro.booking_app_backend.mappers;

import java.util.ArrayList;
import java.util.Arrays;

import com.fernandocanabarro.booking_app_backend.models.dtos.RoomRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.RoomResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;
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
                .hotel(hotel)
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
        return RoomResponseDTO.builder()
                .id(entity.getId())
                .number(entity.getNumber())
                .floor(entity.getFloor())
                .type(entity.getType().getRoomType())
                .pricePerNight(entity.getPricePerNight())
                .description(entity.getDescription())
                .capacity(entity.getCapacity())
                .hotelId(entity.getHotel().getId())
                .build();
    }

}
