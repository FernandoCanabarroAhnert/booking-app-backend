package com.fernandocanabarro.booking_app_backend.services.strategy;

import java.math.BigDecimal;

import com.fernandocanabarro.booking_app_backend.models.entities.BoletoPayment;
import com.fernandocanabarro.booking_app_backend.models.entities.Payment;

public class BoletoPaymentStrategy implements PaymentStrategy {

    @Override
    public Payment processBookingPayment(BigDecimal amount, Integer installmentQuantity) {
        return new BoletoPayment(amount);
    }

}
