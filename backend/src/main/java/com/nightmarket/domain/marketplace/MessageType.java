package com.nightmarket.domain.marketplace;

/**
 * Discriminator for conversation messages.
 */
public enum MessageType {
    TEXT,
    OFFER,
    OFFER_ACCEPTED,
    OFFER_REJECTED,
    MEETUP_COORDINATION,
    SYSTEM
}
