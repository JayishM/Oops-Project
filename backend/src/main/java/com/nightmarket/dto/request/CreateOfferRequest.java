package com.nightmarket.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateOfferRequest {

    @NotBlank(message = "Listing ID is required")
    private String listingId;

    @NotNull(message = "Offer amount is required")
    @DecimalMin(value = "0.01", message = "Offer amount must be greater than zero")
    private BigDecimal offerAmount;

    private String message;

    public CreateOfferRequest() {}

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public BigDecimal getOfferAmount() {
        return offerAmount;
    }

    public void setOfferAmount(BigDecimal offerAmount) {
        this.offerAmount = offerAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
