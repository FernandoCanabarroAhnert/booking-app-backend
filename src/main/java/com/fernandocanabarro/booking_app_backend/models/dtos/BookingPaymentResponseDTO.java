package com.fernandocanabarro.booking_app_backend.models.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingPaymentResponseDTO {

    private Integer paymentType;
    private Integer installmentQuantity; 


}
