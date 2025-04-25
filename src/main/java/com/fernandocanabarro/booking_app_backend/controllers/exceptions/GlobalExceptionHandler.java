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
import com.fernandocanabarro.booking_app_backend.services.exceptions.EmailException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ForbiddenException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.InvalidCurrentPasswordException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.RequiredWorkingHotelIdException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.ResourceNotFoundException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.RoomIsUnavailableForBookingException;
import com.fernandocanabarro.booking_app_backend.services.exceptions.UnauthorizedException;

import jakarta.servlet.http.HttpServletRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmailException.class)
    public ResponseEntity<StandardError> email(EmailException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError error = new StandardError(Instant.now(), status.value(), "Bad Request", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(RequiredWorkingHotelIdException.class)
    public ResponseEntity<StandardError> requiredWorkingHotelId(RequiredWorkingHotelIdException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError error = new StandardError(Instant.now(), status.value(), "Bad Request", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<StandardError> uanuthorized(UnauthorizedException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        StandardError error = new StandardError(Instant.now(), status.value(), "Unauthorized", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<StandardError> forbidden(ForbiddenException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError error = new StandardError(Instant.now(), status.value(), "Forbidden", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> notFound(ResourceNotFoundException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError error = new StandardError(Instant.now(), status.value(), "Not Found", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(RoomIsUnavailableForBookingException.class)
    public ResponseEntity<StandardError> roomIsUnavailableForBookingException(RoomIsUnavailableForBookingException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError error = new StandardError(Instant.now(), status.value(), "Conflict", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(AlreadyExistingPropertyException.class)
    public ResponseEntity<StandardError> alreadyExistingProperty(AlreadyExistingPropertyException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError error = new StandardError(Instant.now(), status.value(), "Conflict", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(InvalidCurrentPasswordException.class)
    public ResponseEntity<StandardError> invalidCurrentPassword(InvalidCurrentPasswordException ex, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        StandardError error = new StandardError(Instant.now(), status.value(), "Conflict", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(status).body(error);
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

}
