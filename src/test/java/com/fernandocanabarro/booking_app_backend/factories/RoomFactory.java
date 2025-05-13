package com.fernandocanabarro.booking_app_backend.factories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import com.fernandocanabarro.booking_app_backend.models.entities.Image;
import com.fernandocanabarro.booking_app_backend.models.entities.Room;
import com.fernandocanabarro.booking_app_backend.models.entities.RoomRating;
import com.fernandocanabarro.booking_app_backend.models.enums.ImageTypeEnum;
import com.fernandocanabarro.booking_app_backend.models.enums.RoomTypeEnum;

public class RoomFactory {

    public static Room createRoom() {
        return Room.builder()
                .id(1L)
                .number("101")
                .floor(1)
                .type(RoomTypeEnum.SINGLE)
                .pricePerNight(BigDecimal.valueOf(100))
                .description("Room description")
                .capacity(1)
                .hotel(HotelFactory.createHotel())
                .images(new ArrayList<Image>(Arrays.asList(new Image(1L, "image.jpg", ImageTypeEnum.ROOM, null, null))))
                .bookings(new ArrayList<>(Arrays.asList()))
                .ratings(new ArrayList<>(Arrays.asList()))
                .build();
    }

    public static RoomRating createRoomRating() {
        return RoomRating.builder()
                .id(1L)
                .room(createRoom())
                .user(UserFactory.createUser())
                .rating(BigDecimal.valueOf(4.5))
                .description("description")
                .createdAt(LocalDateTime.now())
                .build();
    }

}
