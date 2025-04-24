package com.fernandocanabarro.booking_app_backend.models.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fernandocanabarro.booking_app_backend.models.enums.PaymentTypeEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "boleto_payments")
public class BoletoPayment extends Payment {

    private LocalDate expirationDate;

    public BoletoPayment(BigDecimal amount) {
        super(PaymentTypeEnum.BOLETO, amount);
        this.expirationDate = LocalDate.now().plusDays(30);
    }

}
