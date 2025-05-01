package com.fernandocanabarro.booking_app_backend.mappers;

import java.time.LocalDate;

import com.fernandocanabarro.booking_app_backend.models.dtos.credit_card.CreditCardRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.credit_card.CreditCardResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.CreditCard;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.models.enums.CreditCardBrandEnum;

public class CreditCardMapper {

    public static CreditCard convertRequestToEntity(CreditCardRequestDTO request, User user) {
        String year = request.getExpirationDate().substring(0,4);
        String month = request.getExpirationDate().substring(5);
        return CreditCard.builder()
                .holderName(request.getHolderName())
                .cardNumber(request.getCardNumber())
                .brand(CreditCardBrandEnum.fromValue(request.getBrand()))
                .cvv(request.getCvv())
                .expirationDate(LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1))
                .user(user)
                .build();
    }

    public static CreditCardResponseDTO convertEntityToResponseDTO(CreditCard entity) {
        return CreditCardResponseDTO.builder()
                .id(entity.getId())
                .holderName(entity.getHolderName())
                .lastFourDigits("**** **** **** " + entity.getCardNumber().substring(entity.getCardNumber().length() - 4))
                .brand(entity.getBrand().getValue())
                .expirationDate(String.valueOf(entity.getExpirationDate().getMonth().getValue()) + "/" + String.valueOf(entity.getExpirationDate().getYear()))
                .build();
    }

}
