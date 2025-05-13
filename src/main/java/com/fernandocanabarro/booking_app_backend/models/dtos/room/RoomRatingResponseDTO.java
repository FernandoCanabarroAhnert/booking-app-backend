package com.fernandocanabarro.booking_app_backend.models.dtos.room;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoomRatingResponseDTO {

    private Long id;
    private Long roomId;
    private String userFullName;
    private String userEmail;
    private BigDecimal rating;
    private String description;
    private LocalDateTime createdAt;

}
