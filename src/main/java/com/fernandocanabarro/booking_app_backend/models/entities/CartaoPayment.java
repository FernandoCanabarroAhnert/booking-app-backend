package com.fernandocanabarro.booking_app_backend.models.entities;

import java.math.BigDecimal;

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
@Table(name = "cartao_payments")
public class CartaoPayment extends Payment {

    private Integer installmentQuantity;

    public CartaoPayment(BigDecimal amount, Integer installmentQuantity) {
        super(PaymentTypeEnum.CARTAO, amount);
        this.installmentQuantity = installmentQuantity;
    }

}
