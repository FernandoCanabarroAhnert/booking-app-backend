package com.fernandocanabarro.booking_app_backend.services.exceptions;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class RoomIsUnavailableForBookingException extends RuntimeException {

    public RoomIsUnavailableForBookingException(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        super("Room with id " + roomId + 
            " is unavailable for booking between " + 
            checkIn.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " and " + 
            checkOut.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

}
