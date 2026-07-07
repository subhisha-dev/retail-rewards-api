package com.retail.rewards.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = CustomerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCustomerNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("timestamp", LocalDate.now(),
                "status", HttpStatus.NOT_FOUND.value(), "error", "Not Found", "message", e.getMessage()));
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception e) {
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("timestamp", LocalDate.now(),
                "status", HttpStatus.BAD_REQUEST.value(), "error", "Bad Request", "message", e.getMessage()));
    }

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("timestamp", LocalDate.now(),
                "status", HttpStatus.BAD_REQUEST.value(), "error", "Invalid input: months must be 1-12", "message", e.getMessage()));
    }
}
