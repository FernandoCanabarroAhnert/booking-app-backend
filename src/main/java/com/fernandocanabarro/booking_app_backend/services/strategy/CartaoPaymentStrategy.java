package com.fernandocanabarro.booking_app_backend.services.strategy;

import java.math.BigDecimal;

import com.fernandocanabarro.booking_app_backend.models.entities.CartaoPayment;
import com.fernandocanabarro.booking_app_backend.models.entities.Payment;

public class CartaoPaymentStrategy implements PaymentStrategy {

    @Override
    public Payment processBookingPayment(BigDecimal amount, Integer installmentQuantity) {
        return new CartaoPayment(amount, installmentQuantity);
    }

}
