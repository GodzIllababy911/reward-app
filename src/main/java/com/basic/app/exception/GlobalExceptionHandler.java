package com.basic.app.exception;

import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Provides centralized handling of exceptions across the entire application.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);


     /**
     * Handles ResourceNotFoundException and returns an appropriate HTTP 404 response.
     *
     * @param ex The ResourceNotFoundException that was thrown
     * @return A ResponseEntity with HTTP status 404 and the exception message as body
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    /**
     * Handles MethodArgumentTypeMismatchException and returns an appropriate HTTP 400 response.
     * This handles cases where the customerId parameter is not a valid number.
     *
     * @return A ResponseEntity with HTTP status 400 and an error message
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch() {
        log.warn("Method argument type mismatch: {}"," Invalid customerId. It must be a number.");
        
        Map<String, String> error = new HashMap<>();
        error.put("error", "Invalid customerId. It must be a number.");

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Handles IllegalArgumentException and returns an appropriate HTTP 400 response.
     *
     * @param ex The IllegalArgumentException that was thrown
     * @return A ResponseEntity with HTTP status 400 and the exception message
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        
        Map<String, String> error = new HashMap<>();
        error.put("error", ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }
}

