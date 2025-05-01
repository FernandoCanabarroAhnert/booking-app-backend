package com.fernandocanabarro.booking_app_backend.models.entities;

import java.time.LocalDate;

import com.fernandocanabarro.booking_app_backend.models.enums.CreditCardBrandEnum;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "credit_cards")
public class CreditCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String holderName;
    private String cardNumber;
    @Enumerated(EnumType.STRING)
    private CreditCardBrandEnum brand;
    private String cvv;
    private LocalDate expirationDate;
    @ManyToOne
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "fk_credit_card_user", value = ConstraintMode.CONSTRAINT))
    private User user;

}
