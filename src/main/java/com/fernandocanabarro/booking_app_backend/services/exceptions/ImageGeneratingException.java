package com.fernandocanabarro.booking_app_backend.services.exceptions;

public class ImageGeneratingException extends RuntimeException {

    public ImageGeneratingException() {
        super("Error while generating image");
    }

}
