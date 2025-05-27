package com.fernandocanabarro.booking_app_backend.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.booking_app_backend.models.entities.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT obj FROM Booking obj JOIN obj.payment p " + 
        "WHERE obj.checkIn >= :checkIn " +
        "AND obj.checkOut <= :checkOut " +
        "AND (:hotelId IS NULL OR obj.room.hotel.id = :hotelId) " +
        "AND p.amount BETWEEN :minPrice AND :maxPrice " +
        "AND (:paymentType IS NULL OR p.paymentType IN :paymentType)"
    )
    Page<Booking> findAllBookingsWithQuery(Pageable pageable, LocalDate checkIn, LocalDate checkOut, Long hotelId, 
        BigDecimal minPrice, BigDecimal maxPrice, List<String> paymentType);

    @Query("SELECT MAX(amount) FROM Payment")
    BigDecimal findMaxPaymentAmount();

    @Query("SELECT MIN(amount) FROM Payment")
    BigDecimal findMinPaymentAmount();

    @Query("SELECT MIN(checkIn) FROM Booking")
    LocalDate findMinCheckInDate();

    @Query("SELECT MAX(checkOut) FROM Booking")
    LocalDate findMaxCheckOutDate();

    @Query("SELECT obj FROM Booking obj WHERE obj.user.id = :userId")
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT obj FROM Booking obj WHERE obj.room.id = :roomId")
    Page<Booking> findByRoomId(Long roomId, Pageable pageable);

}
