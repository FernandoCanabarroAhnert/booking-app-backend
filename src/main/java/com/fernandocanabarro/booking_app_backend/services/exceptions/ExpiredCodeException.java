package com.fernandocanabarro.booking_app_backend.services.exceptions;

public class ExpiredCodeException extends RuntimeException {

    public ExpiredCodeException(String message) {
        super(message);
    }

}
