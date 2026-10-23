package com.rafael.todo.shared;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorDto> handleDomainException(DomainException exception) {
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
