package com.fernandocanabarro.booking_app_backend.mappers;

import com.fernandocanabarro.booking_app_backend.models.dtos.image.ImageResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.Image;

public class ImageMapper {

    public static ImageResponseDTO convertEntityResponseDTO(Image entity) {
        return ImageResponseDTO.builder()
                .id(entity.getId())
                .base64Image(entity.getBase64Image())
                .build();
    }

}
