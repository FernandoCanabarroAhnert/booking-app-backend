package com.fernandocanabarro.booking_app_backend.factories;

import java.time.LocalDate;

import com.fernandocanabarro.booking_app_backend.models.entities.CreditCard;
import com.fernandocanabarro.booking_app_backend.models.enums.CreditCardBrandEnum;

public class CreditCardFactory {

    public static CreditCard createCreditCard() {
        return CreditCard.builder()
                .id(1L)
                .cardNumber("1234567812345678")
                .holderName("name")
                .expirationDate(LocalDate.of(2025, 12, 31))
                .cvv("123")
                .user(UserFactory.createUser())
                .brand(CreditCardBrandEnum.MASTERCARD)
                .build();
    }

}
