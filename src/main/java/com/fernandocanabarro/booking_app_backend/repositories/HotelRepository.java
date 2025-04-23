package com.fernandocanabarro.booking_app_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

}
