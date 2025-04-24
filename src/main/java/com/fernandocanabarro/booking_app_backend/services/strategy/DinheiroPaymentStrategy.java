package com.fernandocanabarro.booking_app_backend.services.strategy;

import java.math.BigDecimal;

import com.fernandocanabarro.booking_app_backend.models.entities.DinheiroPayment;
import com.fernandocanabarro.booking_app_backend.models.entities.Payment;

public class DinheiroPaymentStrategy implements PaymentStrategy {

    @Override
    public Payment processBookingPayment(BigDecimal amount, Integer installmentQuantity) {
        return new DinheiroPayment(amount);
    }

}
