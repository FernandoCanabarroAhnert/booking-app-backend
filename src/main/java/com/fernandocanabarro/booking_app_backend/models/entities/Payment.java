package com.fernandocanabarro.booking_app_backend.models.entities;

import java.math.BigDecimal;

import com.fernandocanabarro.booking_app_backend.models.enums.PaymentTypeEnum;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "payments")
public abstract class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PaymentTypeEnum paymentType;

    private BigDecimal amount;

    public Payment(PaymentTypeEnum paymentType, BigDecimal amount) {
        this.paymentType = paymentType;
        this.amount = amount;
    }

}
