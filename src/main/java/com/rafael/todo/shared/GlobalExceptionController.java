package com.rafael.todo.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.java.Log;

@RestControllerAdvice
@Log
public class GlobalExceptionController {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorDto> handleDomainException(DomainException exception) {
        log.warning(String.format("Domain exception caught [%s]: %s (HTTP %d)", 
            exception.getCode(), exception.getMessage(), exception.getStatusCode()));
        var error = new ErrorDto(
            exception.getCode(),
            exception.getMessage(),
            exception.getStatusCode()
        );
        return ResponseEntity
            .status(exception.getStatusCode())
            .body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> handleUnexpectedException(Exception exception) {
        log.severe("Unexpected server error: " + exception.getMessage());
        var error = new ErrorDto(
            "internal.server.error",
            "An unexpected error occurred",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
    }

}
