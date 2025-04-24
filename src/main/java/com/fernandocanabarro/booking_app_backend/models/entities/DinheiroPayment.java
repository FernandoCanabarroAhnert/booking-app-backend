package com.fernandocanabarro.booking_app_backend.models.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "dinheiro_payments")
public class DinheiroPayment extends Payment {

}
