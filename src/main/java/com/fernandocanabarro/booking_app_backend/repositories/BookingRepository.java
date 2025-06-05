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
import com.fernandocanabarro.booking_app_backend.projections.BookingStatsSummaryProjection;

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

    @Query(nativeQuery = true, value = """
        SELECT EXTRACT(MONTH FROM b.check_in) "month", SUM(p.amount) "amount", COUNT(b.id) "booking_quantity", SUM(guests_quantity) "guests"
        FROM payments p
        INNER JOIN bookings b ON b.payment_id = p.id
        INNER JOIN rooms r ON b.room_id = r.id
        WHERE (:hotelId IS NULL OR r.hotel_id = :hotelId)
        GROUP BY 1
        ORDER BY 1        
    """)
    List<BookingStatsSummaryProjection> getBookingStatsSummary(Long hotelId);

    @Query(nativeQuery = true, value = """
        SELECT
            ROUND(
                (
                    SELECT COUNT(r.id) "occupied_rooms"
                    FROM rooms r
                    INNER JOIN bookings b ON b.room_id = r.id
                    WHERE (:hotelId IS NULL OR hotel_id = :hotelId)
                    AND CURRENT_DATE BETWEEN b.check_in AND b.check_out
                )::numeric
                /
                (
                    SELECT COUNT(*) FROM rooms
                    WHERE (:hotelId IS NULL OR hotel_id = :hotelId)
                )::numeric * 100,
                2
            ) "occupation_percentage"        
    """)
    BigDecimal getRoomOccupationPercentage(Long hotelId);

    @Query(nativeQuery = true, value = """
        SELECT SUM(p.amount)
        FROM payments p
        INNER JOIN bookings b ON b.payment_id = p.id
        INNER JOIN rooms r ON b.room_id = r.id
        WHERE (:hotelId IS NULL OR r.hotel_id = :hotelId)        
    """)
    BigDecimal getTotalPaymentsAmount(Long hotelId);

    @Query(nativeQuery = true, value = """
        SELECT ROUND(AVG(b.check_out - b.check_in), 2)
        FROM bookings b
        INNER JOIN rooms r ON b.room_id = r.id
        WHERE (:hotelId IS NULL OR r.hotel_id = :hotelId)     
    """)
    BigDecimal getAverageStayDays(Long hotelId);

}
