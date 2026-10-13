package com.nightmarket.exception;

public class UnauthorizedListingAccessException extends RuntimeException {
    public UnauthorizedListingAccessException(String message) {
        super(message);
    }
}
