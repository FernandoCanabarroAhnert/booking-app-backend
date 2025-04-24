package com.fernandocanabarro.booking_app_backend.services.strategy;

import java.math.BigDecimal;

import com.fernandocanabarro.booking_app_backend.models.entities.Payment;
import com.fernandocanabarro.booking_app_backend.models.entities.PixPayment;

public class PixPaymentStrategy implements PaymentStrategy {

    @Override
    public Payment processBookingPayment(BigDecimal amount, Integer installmentQuantity) {
        return new PixPayment(amount);
    }

}
