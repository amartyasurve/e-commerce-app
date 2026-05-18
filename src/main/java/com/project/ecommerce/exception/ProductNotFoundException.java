package com.project.ecommerce.exception;

/**
 * Thrown when a requested product is not found in the database.
 */
public class ProductNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ProductNotFoundException(String message) {
        super(message);
    }

    public ProductNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ProductNotFoundException(Long productId) {
        super("Product not found with id: " + productId);
    }
}

