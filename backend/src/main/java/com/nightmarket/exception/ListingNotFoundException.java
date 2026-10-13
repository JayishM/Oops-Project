package com.nightmarket.exception;

public class ListingNotFoundException extends RuntimeException {
    public ListingNotFoundException(String listingId) {
        super("Listing not found with ID: " + listingId);
    }
}
