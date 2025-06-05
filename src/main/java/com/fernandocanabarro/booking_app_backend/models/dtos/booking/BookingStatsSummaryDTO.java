package com.fernandocanabarro.booking_app_backend.models.dtos.booking;

import java.math.BigDecimal;
import java.math.RoundingMode;

import com.fernandocanabarro.booking_app_backend.projections.BookingStatsSummaryProjection;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingStatsSummaryDTO {

    private Integer month;
    private BigDecimal amount;
    private Long bookingQuantity;
    private Long guests;

    public BookingStatsSummaryDTO(BookingStatsSummaryProjection projection) {
        this.month = projection.getMonth();
        this.amount = projection.getAmount().divide(BigDecimal.valueOf(1000), 3, RoundingMode.HALF_UP);
        this.bookingQuantity = projection.getBookingQuantity();
        this.guests = projection.getGuests();
    }

}
