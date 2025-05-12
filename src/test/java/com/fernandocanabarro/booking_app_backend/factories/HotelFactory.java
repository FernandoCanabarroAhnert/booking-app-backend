package com.fernandocanabarro.booking_app_backend.factories;

import java.util.ArrayList;
import java.util.Arrays;

import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;
import com.fernandocanabarro.booking_app_backend.models.entities.Image;
import com.fernandocanabarro.booking_app_backend.models.enums.ImageTypeEnum;

public class HotelFactory {

    public static Hotel createHotel() {
        return Hotel.builder()
                .id(1L)
                .name("name")
                .description("description")
                .roomQuantity(10)
                .street("street")
                .number("number")
                .city("city")
                .zipCode("zipCode")
                .state("state")
                .phone("(11) 99999-9999")
                .images(new ArrayList<Image>(Arrays.asList(new Image(1L, "image.jpg", ImageTypeEnum.HOTEL, null, null))))
                .build();
    }

}
