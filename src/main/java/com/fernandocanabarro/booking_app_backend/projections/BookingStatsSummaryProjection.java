package com.fernandocanabarro.booking_app_backend.projections;

import java.math.BigDecimal;

public interface BookingStatsSummaryProjection {

    Integer getMonth();
    BigDecimal getAmount();
    Long getBookingQuantity();
    Long getGuests();

}
