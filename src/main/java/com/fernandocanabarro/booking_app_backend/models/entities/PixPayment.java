package com.fernandocanabarro.booking_app_backend.models.entities;

import java.math.BigDecimal;

import com.fernandocanabarro.booking_app_backend.models.enums.PaymentTypeEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "pix_payments")
public class PixPayment extends Payment {

    public PixPayment() {}

    public PixPayment(BigDecimal amount) {
        super(PaymentTypeEnum.PIX, amount);
    }

}
