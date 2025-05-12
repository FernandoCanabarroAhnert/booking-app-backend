package com.fernandocanabarro.booking_app_backend.models.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "activation_codes")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private boolean used;
    private LocalDateTime usedAt;

    public boolean isValid() {
        return this.expiresAt.isAfter(LocalDateTime.now());
    }

}
