package com.fernandocanabarro.booking_app_backend.repositories;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fernandocanabarro.booking_app_backend.models.entities.RoomRating;

@Repository
public interface RoomRatingRepository extends JpaRepository<RoomRating, Long> {

    @Query("SELECT obj FROM RoomRating obj WHERE obj.room.id = :roomId")
    Page<RoomRating> findAllByRoomId(Long roomId, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT ROUND(AVG(rating), 1) FROM room_ratings")
    BigDecimal findAverageRating();

    @Query("SELECT obj FROM RoomRating obj WHERE obj.user.id = :userId")
    Page<RoomRating> findAllByUserId(Long userId, Pageable pageable);

}
