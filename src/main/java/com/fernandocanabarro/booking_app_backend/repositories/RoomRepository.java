package com.fernandocanabarro.booking_app_backend.repositories;

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

}
