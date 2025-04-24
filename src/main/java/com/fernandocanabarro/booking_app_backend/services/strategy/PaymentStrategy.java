package com.fernandocanabarro.booking_app_backend.services.strategy;

import java.math.BigDecimal;

import com.fernandocanabarro.booking_app_backend.models.entities.Payment;

public interface PaymentStrategy {

    Payment processBookingPayment(BigDecimal amount, Integer installmentQuantity);

}
