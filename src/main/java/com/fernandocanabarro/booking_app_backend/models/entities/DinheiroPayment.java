package com.fernandocanabarro.booking_app_backend.models.entities;

import java.math.BigDecimal;

import com.fernandocanabarro.booking_app_backend.models.enums.PaymentTypeEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "dinheiro_payments")
public class DinheiroPayment extends Payment {

    public DinheiroPayment() {}

    public DinheiroPayment(BigDecimal amount) {
        super(PaymentTypeEnum.DINHEIRO, amount);
    }

}
