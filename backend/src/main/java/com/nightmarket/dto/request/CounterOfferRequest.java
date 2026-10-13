package com.nightmarket.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CounterOfferRequest {

    @NotNull(message = "Counter amount is required")
    @DecimalMin(value = "0.01", message = "Counter amount must be positive")
    private BigDecimal counterAmount;

    private String message;

    public CounterOfferRequest() {}

    public BigDecimal getCounterAmount() {
        return counterAmount;
    }

    public void setCounterAmount(BigDecimal counterAmount) {
        this.counterAmount = counterAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
