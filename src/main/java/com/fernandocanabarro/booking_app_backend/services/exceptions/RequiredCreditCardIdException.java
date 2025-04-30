package com.fernandocanabarro.booking_app_backend.services.exceptions;

public class RequiredCreditCardIdException extends RuntimeException {

    public RequiredCreditCardIdException() {
        super("Required field: creditCardId");
    }

}
