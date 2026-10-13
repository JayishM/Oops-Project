package com.nightmarket.domain.interfaces;

import java.math.BigDecimal;

/**
 * Interface defining pricing negotiability contract.
 * Demonstrates OOP Abstraction and Polymorphism.
 */
public interface Negotiable {
    /**
     * Checks if the seller has marked the price as negotiable.
     */
    boolean isPriceNegotiable();

    /**
     * Calculates the minimum reasonable floor price for negotiations.
     */
    BigDecimal getMinimumAcceptablePrice();
}
