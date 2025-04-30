package com.fernandocanabarro.booking_app_backend.models.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fernandocanabarro.booking_app_backend.models.enums.CreditCardBrandEnum;
import com.fernandocanabarro.booking_app_backend.models.enums.PaymentTypeEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private Long creditCardId;
    private String cardHolderName;
    private String lastFourDigits;
    @Enumerated(EnumType.STRING)
    private CreditCardBrandEnum brand;
    private LocalDate expirationDate;

    public CartaoPayment(BigDecimal amount, Integer installmentQuantity, boolean isOnlinePayment) {
        super(PaymentTypeEnum.CARTAO, amount, isOnlinePayment);
        this.installmentQuantity = installmentQuantity;
    }

}
