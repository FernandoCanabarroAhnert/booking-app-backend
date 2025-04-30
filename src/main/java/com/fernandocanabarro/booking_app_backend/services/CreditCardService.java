package com.fernandocanabarro.booking_app_backend.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fernandocanabarro.booking_app_backend.models.dtos.credit_card.CreditCardRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.credit_card.CreditCardResponseDTO;

public interface CreditCardService {

    Page<CreditCardResponseDTO> getConnectedUserCreditCards(Pageable pageable);
    void addCreditCard(CreditCardRequestDTO request);
    void deleteCreditCard(Long id);

}
