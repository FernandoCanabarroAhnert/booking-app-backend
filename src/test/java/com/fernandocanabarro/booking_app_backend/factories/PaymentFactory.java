package com.fernandocanabarro.booking_app_backend.factories;

import java.math.BigDecimal;

import com.fernandocanabarro.booking_app_backend.models.entities.DinheiroPayment;

public class PaymentFactory {

    public static DinheiroPayment createDinheiroPayment() {
        DinheiroPayment payment = new DinheiroPayment(BigDecimal.valueOf(600),false);
        payment.setId(1L);
        return payment;
    }

}
