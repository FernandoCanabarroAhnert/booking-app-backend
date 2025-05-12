package com.fernandocanabarro.booking_app_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.booking_app_backend.models.entities.ActivationCode;

@Repository
public interface ActivationCodeRepository extends JpaRepository<ActivationCode, Long> {

    Optional<ActivationCode> findByCode(String code);

}
