package com.fernandocanabarro.booking_app_backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.booking_app_backend.models.entities.CreditCard;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    @Query("SELECT obj FROM CreditCard obj WHERE obj.user.id = :userId")
    Page<CreditCard> findByUser(Long userId, Pageable pageable);

}
