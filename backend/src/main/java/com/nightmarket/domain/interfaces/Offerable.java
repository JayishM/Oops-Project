package com.nightmarket.domain.interfaces;

import com.nightmarket.exception.InvalidOfferException;
import java.math.BigDecimal;

/**
 * Interface defining rules for items that accept monetary price offers.
 * Demonstrates Abstraction and Encapsulation of pricing constraints.
 */
public interface Offerable {
    /**
     * Determines whether the item is in an eligible state to receive new offers.
     */
    boolean canReceiveOffers();

    /**
     * Validates that a submitted offer amount satisfies business rules.
     * @throws InvalidOfferException if the offer is invalid
     */
    void validateOfferAmount(BigDecimal offerAmount) throws InvalidOfferException;
}
