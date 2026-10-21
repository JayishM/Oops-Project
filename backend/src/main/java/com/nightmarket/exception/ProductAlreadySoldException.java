package com.nightmarket.exception;

public class ProductAlreadySoldException extends RuntimeException {
    public ProductAlreadySoldException(String listingId) {
        super("Listing " + listingId + " is already sold or no longer available for offers.");
    }
}
