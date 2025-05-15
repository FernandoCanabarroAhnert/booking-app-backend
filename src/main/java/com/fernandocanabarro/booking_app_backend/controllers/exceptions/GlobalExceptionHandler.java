package com.fernandocanabarro.booking_app_backend.controllers.exceptions;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fernandocanabarro.booking_app_backend.models.dtos.exceptions.StandardError;
import com.fernandocanabarro.booking_app_backend.models.dtos.exceptions.ValidationError;
import com.fernandocanabarro.booking_app_backend.services.exceptions.AlreadyExistingPropertyException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.BadRequestException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.EmailException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ExpiredCodeException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ForbiddenException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ImageGeneratingException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.InvalidCurrentPasswordException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.RequiredWorkingHotelIdException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.RoomIsUnavailableForBookingException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
        EmailException.class,
        ImageGeneratingException.class,
        RequiredWorkingHotelIdException.class,
        BadRequestException.class,
        ExpiredCodeException.class
    })
    public ResponseEntity<StandardError> badRequest(RuntimeException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return this.buildStandardError(status, "Bad Request", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<StandardError> unauthorized(UnauthorizedException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        return this.buildStandardError(status, "Unauthorized", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<StandardError> forbidden(ForbiddenException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return this.buildStandardError(status, "Forbidden", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> notFound(ResourceNotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return this.buildStandardError(status, "Not Found", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler({
        RoomIsUnavailableForBookingException.class,
        AlreadyExistingPropertyException.class,
        InvalidCurrentPasswordException.class
    })
    public ResponseEntity<StandardError> conflict(RuntimeException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        return this.buildStandardError(status, "Conflict", ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> invalidData(MethodArgumentNotValidException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError error = new ValidationError(Instant.now(), status.value(), "Unprocessable Entity", "Invalid Data", request.getRequestURI());
        for (FieldError f : ex.getBindingResult().getFieldErrors()) {
            error.addError(f.getField(), f.getDefaultMessage());
        }
        return ResponseEntity.status(status).body(error);
    }

    private ResponseEntity<StandardError> buildStandardError(HttpStatus status, String title, String message, String path) {
        StandardError error = new StandardError(Instant.now(), status.value(), title, message, path);
        return ResponseEntity.status(status).body(error);
    }

}
