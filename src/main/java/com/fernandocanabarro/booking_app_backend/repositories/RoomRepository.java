package com.fernandocanabarro.booking_app_backend.repositories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.booking_app_backend.models.entities.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("SELECT room FROM Room room where room.hotel.id = :hotelId")
    Page<Room> findByHotelId(Long hotelId, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT MAX(price_per_night) FROM rooms")
    BigDecimal findMaxPricePerNight();

    @Query(nativeQuery = true, value = "SELECT MIN(price_per_night) FROM rooms")
    BigDecimal findMinPricePerNight();

    @Query("SELECT obj FROM Room obj JOIN obj.hotel h " + 
        "WHERE (:types IS NULL OR obj.type IN :types) " + 
        "AND (:capacity IS NULL OR obj.capacity = :capacity) " + 
        "AND obj.pricePerNight BETWEEN :minPrice AND :maxPrice " + 
        "AND (:city IS NULL OR LOWER(h.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
        "AND (:hotelId IS NULL OR h.id = :hotelId)")
    Page<Room> findByTypeOrCapacityOrPricePerNightOrByHotelCity(List<String> types, Integer capacity, BigDecimal minPrice, BigDecimal maxPrice, String city, Long hotelId, Pageable pageable);
}
