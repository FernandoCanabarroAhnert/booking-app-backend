package com.fernandocanabarro.booking_app_backend.models.dtos.base;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseBookingResponseDTO {

    private Long id;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Integer guestsQuantity;
    private LocalDateTime createdAt;
    private boolean isFinished;
    private BigDecimal totalPrice;

}
