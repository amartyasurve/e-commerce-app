package com.project.ecommerce.exception;

/**
 * Thrown when data ingestion from external API fails.
 */
public class DataIngestionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DataIngestionException(String message) {
        super(message);
    }

    public DataIngestionException(String message, Throwable cause) {
        super(message, cause);
    }
}

