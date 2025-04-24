package com.fernandocanabarro.booking_app_backend.services.exceptions;

public class AlreadyExistingPropertyException extends RuntimeException {

    public AlreadyExistingPropertyException(String property) {
        super(property + " already exists");
    }

}
