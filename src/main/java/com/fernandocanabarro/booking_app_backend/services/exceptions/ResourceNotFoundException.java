package com.fernandocanabarro.booking_app_backend.services.exceptions;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String object, Long id) {
        super(object + " with id " + id + " not found.");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }

}
