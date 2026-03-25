package com.basic.app.exception;

/**
 * Custom exception thrown when a requested resource is not found in the system.
 * Extends RuntimeException to indicate that this is an unchecked exception.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs a new ResourceNotFoundException with the specified detail message.
     *
     * @param message The detail message explaining which resource was not found
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
