package com.nightmarket.exception;

/**
 * Base custom exception for domain validation failures.
 * Demonstrates Java Exception Hierarchy and custom error handling.
 */
public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
    }

    public DomainValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
