package com.fernandocanabarro.booking_app_backend.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.booking_app_backend.models.entities.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT obj FROM Booking obj WHERE obj.user.id = :userId")
    Page<Booking> findByUserId(Long userId, Pageable pageable);

    @Query("SELECT obj FROM Booking obj WHERE obj.room.id = :roomId")
    Page<Booking> findByRoomId(Long roomId, Pageable pageable);

}
