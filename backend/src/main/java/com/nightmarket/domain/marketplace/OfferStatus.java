package com.nightmarket.domain.marketplace;

/**
 * State machine representing an offer negotiation lifecycle.
 */
public enum OfferStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    COUNTERED,
    CANCELLED,
    EXPIRED;

    public boolean isTerminal() {
        return this == ACCEPTED || this == REJECTED || this == CANCELLED || this == EXPIRED;
    }
}
