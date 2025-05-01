package com.fernandocanabarro.booking_app_backend.services.exceptions;

public class InvalidPaymentException extends RuntimeException {
    
    public InvalidPaymentException(String message) {
        super(message);
    }

}
