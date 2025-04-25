package com.fernandocanabarro.booking_app_backend.services.exceptions;

public class RequiredWorkingHotelIdException extends RuntimeException {

    public RequiredWorkingHotelIdException() {
        super("When creating a user with Operator or Admin role, the working hotel Id is required.");
    }

}
