package com.fernandocanabarro.booking_app_backend.models.dtos.credit_card;

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
public class CreditCardResponseDTO {

    private Long id;
    private String holderName;
    private String lastFourDigits;
    private Integer brand;
    private String expirationDate;

}
