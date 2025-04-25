package com.fernandocanabarro.booking_app_backend.services.exceptions;

public class ExpiredCodeException extends RuntimeException {

    public ExpiredCodeException() {
        super("The code is expired.");
    }

}
