package com.fernandocanabarro.booking_app_backend.services.exceptions;

public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }

}
