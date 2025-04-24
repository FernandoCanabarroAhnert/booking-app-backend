package com.fernandocanabarro.booking_app_backend.models.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fernandocanabarro.booking_app_backend.models.enums.RoomTypeEnum;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "rooms")
@EqualsAndHashCode(of = "id")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private Integer floor;
    @Enumerated(EnumType.STRING)
    private RoomTypeEnum type;
    private BigDecimal pricePerNight;
    private String description;
    private Integer capacity;
    @OneToMany(mappedBy = "room", fetch = FetchType.LAZY)
    private List<Booking> bookings;
    @ManyToOne
    @JoinColumn(name = "hotel_id", foreignKey = @ForeignKey(name = "fk_room_hotel", value = ConstraintMode.CONSTRAINT))
    private Hotel hotel;

    public boolean isAvalableToBook(LocalDate checkIn, LocalDate checkOut) {
        List<Booking> bookings = this.bookings.stream()
            .filter(booking -> !booking.isFinished())
            .filter(booking -> checkIn.isBefore(booking.getCheckOut().plusDays(1)) && checkOut.plusDays(1).isAfter(booking.getCheckIn()))
            .toList();
        return bookings.isEmpty();
    }

    public List<LocalDate> getUnavailableDates() {
        return this.bookings.stream()
            .filter(booking -> !booking.isFinished())
            .flatMap(booking -> booking.getCheckIn().datesUntil(booking.getCheckOut().plusDays(1L)))
            .toList();
    }

}
