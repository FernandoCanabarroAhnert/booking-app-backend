package com.fernandocanabarro.booking_app_backend.models.dtos.hotel;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseHotelResponse;
import com.fernandocanabarro.booking_app_backend.models.dtos.image.ImageResponseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HotelResponseDTO extends BaseHotelResponse {

    private ImageResponseDTO cardDisplayImage;

}
