package com.fernandocanabarro.booking_app_backend.models.dtos.booking;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingPaymentRequestDTO {

    @NotNull(message = "Required field")
    private Boolean isOnlinePayment;

    @Min(1)
    @Max(4)
    @NotNull(message = "Required field")
    private Integer paymentType;

    private Integer installmentQuantity; 

    private Long creditCardId;


}
