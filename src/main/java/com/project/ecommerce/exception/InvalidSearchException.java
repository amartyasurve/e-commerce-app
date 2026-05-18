package com.project.ecommerce.exception;

/**
 * Thrown when invalid search parameters are provided.
 */
public class InvalidSearchException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InvalidSearchException(String message) {
        super(message);
    }

    public InvalidSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}

