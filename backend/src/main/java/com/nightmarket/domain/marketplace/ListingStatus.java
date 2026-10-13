package com.nightmarket.domain.marketplace;

/**
 * State machine for marketplace listings.
 * Validates allowed state transitions in domain methods.
 */
public enum ListingStatus {
    ACTIVE,
    RESERVED,
    SOLD,
    HIDDEN_BY_REPORTS,
    EXPIRED;

    public boolean isAvailableForBrowse() {
        return this == ACTIVE;
    }

    public boolean canAcceptOffers() {
        return this == ACTIVE;
    }
}
