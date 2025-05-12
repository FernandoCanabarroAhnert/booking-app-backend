package com.fernandocanabarro.booking_app_backend.models.dtos.room;

import java.time.LocalDate;
import java.util.List;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseRoomResponse;
import com.fernandocanabarro.booking_app_backend.models.dtos.hotel.HotelResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.image.ImageResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomDetailResponseDTO extends BaseRoomResponse {

    private HotelResponseDTO hotel;
    private List<LocalDate> unavailableDates;
    private List<ImageResponseDTO> images;

}
