package com.fernandocanabarro.booking_app_backend.models.dtos.booking;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingDashboardSummaryDTO {

    private BigDecimal occupationPercentage;
    private BigDecimal totalAmount;
    BigDecimal averageStayDays;
    BigDecimal averageRating;
    List<BookingStatsSummaryDTO> bookingStatsSummaries;

}
