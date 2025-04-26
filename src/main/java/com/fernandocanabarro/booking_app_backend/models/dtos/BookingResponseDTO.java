package com.fernandocanabarro.booking_app_backend.models.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
public class BookingResponseDTO {

    private Long id;
    private UserResponseDTO user;
    private RoomResponseDTO room;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private LocalDateTime createdAt;
    private boolean isFinished;
    private BigDecimal totalPrice;
    private BookingPaymentResponseDTO payment;

}
