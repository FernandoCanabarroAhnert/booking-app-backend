package com.fernandocanabarro.booking_app_backend.models.entities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.ConstraintMode;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.ForeignKey;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "guest_id", foreignKey = @ForeignKey(name = "fk_booking_guest", value = ConstraintMode.CONSTRAINT))
    private Guest guest;
    @ManyToOne
    @JoinColumn(name = "room_id", foreignKey = @ForeignKey(name = "fk_booking_room", value = ConstraintMode.CONSTRAINT))
    private Room room;

    private LocalDate checkIn;
    private LocalDate checkOut;

    private LocalDateTime createdAt;
    private boolean isFinished;

    public BigDecimal getTotalPrice() {
        return this.room.getPricePerNight().multiply(BigDecimal.valueOf(this.checkIn.datesUntil(this.checkOut).count()));
    }

}
