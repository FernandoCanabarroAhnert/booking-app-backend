package com.fernandocanabarro.booking_app_backend.tests;

import static org.mockito.Mockito.when;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.fernandocanabarro.booking_app_backend.factories.CreditCardFactory;
import com.fernandocanabarro.booking_app_backend.factories.UserFactory;
import com.fernandocanabarro.booking_app_backend.models.dtos.credit_card.CreditCardRequestDTO;
import com.fernandocanabarro.booking_app_backend.models.dtos.credit_card.CreditCardResponseDTO;
import com.fernandocanabarro.booking_app_backend.models.entities.CreditCard;
import com.fernandocanabarro.booking_app_backend.models.entities.User;
import com.fernandocanabarro.booking_app_backend.repositories.CreditCardRepository;
import com.fernandocanabarro.booking_app_backend.services.AuthService;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.services.impl.CreditCardServiceImpl;

@ExtendWith(MockitoExtension.class)
public class CreditCardServiceTests {

    @InjectMocks
    private CreditCardServiceImpl creditCardService;
    @Mock
    private CreditCardRepository creditCardRepository;
    @Mock
    private AuthService authService;

    private User user;
    private CreditCard creditCard;
    private PageImpl<CreditCard> page;

    @BeforeEach
    public void setup() {
        this.user = UserFactory.createUser();
        this.creditCard = CreditCardFactory.createCreditCard();
        this.page = new PageImpl<>(List.of(creditCard));
    }

    @Test
    public void getConnectedUserCreditCardsShouldReturnPageOfCreditCardResponseDTO() {
        Pageable pageable = PageRequest.of(0,10);
        when(authService.getConnectedUser()).thenReturn(user);
        when(creditCardRepository.findByUser(user.getId(), pageable)).thenReturn(page);

        Page<CreditCardResponseDTO> response = creditCardService.getConnectedUserCreditCards(pageable);

        assertThat(response).isNotNull();
        assertThat(response.getContent().get(0).getId()).isEqualTo(1);
        assertThat(response.getContent().get(0).getLastFourDigits()).isEqualTo("**** **** **** 5678");
        assertThat(response.getContent().get(0).getHolderName()).isEqualTo("name");
        assertThat(response.getContent().get(0).getBrand()).isEqualTo(2);
    }

    @Test
    public void addCreditCardShouldThrowNoException() {
        CreditCardRequestDTO request = new CreditCardRequestDTO("name", "1234567812345678", "321", "2025-12", 1);
        assertThatCode(() -> creditCardService.addCreditCard(request)).doesNotThrowAnyException();
    }

    @Test
    public void deleteCreditCardShouldThrowNoException() {
        when(creditCardRepository.existsById(1L)).thenReturn(true);
        assertThatCode(() -> creditCardService.deleteCreditCard(1L)).doesNotThrowAnyException();
    }

    @Test
    public void deleteCreditCardShouldThrowResourceNotFoundException() {
        when(creditCardRepository.existsById(1L)).thenReturn(false);
        assertThatThrownBy(() -> creditCardService.deleteCreditCard(1L)).isInstanceOf(ResourceNotFoundException.class);
    }
}
