package com.fernandocanabarro.booking_app_backend.models.dtos;

import com.fernandocanabarro.booking_app_backend.models.dtos.base.BaseRoomResponse;

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
public class RoomResponseDTO extends BaseRoomResponse {
    
    private ImageResponseDTO cardDisplayImage;

}
