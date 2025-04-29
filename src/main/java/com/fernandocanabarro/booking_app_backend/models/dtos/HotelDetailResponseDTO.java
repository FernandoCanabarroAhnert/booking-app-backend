package com.fernandocanabarro.booking_app_backend.models.dtos;

import java.util.List;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseHotelResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HotelDetailResponseDTO extends BaseHotelResponse {

    private List<ImageResponseDTO> images;

}
