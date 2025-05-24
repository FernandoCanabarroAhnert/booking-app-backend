package com.fernandocanabarro.booking_app_backend.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.booking_app_backend.models.entities.Hotel;
import com.fernandocanabarro.booking_app_backend.projections.HotelSearchProjection;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    Page<Hotel> findAllByNameContainingIgnoreCase(String name, Pageable pageable);

    List<HotelSearchProjection> findAllByNameContainingIgnoreCase(String name);

}
