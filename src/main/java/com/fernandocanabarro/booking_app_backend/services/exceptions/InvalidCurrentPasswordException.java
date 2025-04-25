package com.fernandocanabarro.booking_app_backend.services.exceptions;

public class InvalidCurrentPasswordException extends RuntimeException {

    public InvalidCurrentPasswordException() {
        super("Invalid current password");
    }

}
