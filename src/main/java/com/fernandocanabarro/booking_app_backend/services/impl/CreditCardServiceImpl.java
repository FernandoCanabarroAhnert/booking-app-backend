package com.fernandocanabarro.booking_app_backend.services.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fernandocanabarro.booking_app_backend.mappers.CreditCardMapper;
import com.fernandocanabarro.booking_app_backend.models.dtos.credit_card.CreditCardRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.credit_card.CreditCardResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.CreditCard;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.repositories.CreditCardRepository;
import com.fernandocanabarro.booking_app_backend.services.AuthService;
import com.fernandocanabarro.booking_app_backend.services.CreditCardService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CreditCardServiceImpl implements CreditCardService {

    private final CreditCardRepository creditCardRepository;
    private final AuthService authService;

    @Override
    @Transactional(readOnly = true)
    public Page<CreditCardResponseDTO> getConnectedUserCreditCards(Pageable pageable) {
        User user = this.authService.getConnectedUser();
        return this.creditCardRepository.findByUser(user.getId(), pageable)
            .map(CreditCardMapper::convertEntityToResponseDTO);
    }

    @Override
    @Transactional
    public void addCreditCard(CreditCardRequestDTO request) {
        int month = Integer.parseInt(request.getExpirationDate().substring(5));
        if (month > 12) throw new BadRequestException("Invalid expiration month"); 
        User user = this.authService.getConnectedUser();
        CreditCard creditCard = CreditCardMapper.convertRequestToEntity(request, user);
        this.creditCardRepository.save(creditCard);
    }

    @Override
    @Transactional
    public void deleteCreditCard(Long id) {
        CreditCard creditCard = this.creditCardRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Credit card not found"));
        this.authService.verifyIfConnectedUserHasAdminPermission(creditCard.getUser().getId());
        this.creditCardRepository.deleteById(id);
    }


}
