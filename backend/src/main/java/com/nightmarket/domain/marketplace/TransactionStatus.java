package com.nightmarket.domain.marketplace;

/**
 * Peer-to-peer campus transaction workflow state machine.
 */
public enum TransactionStatus {
    INITIATED,
    AGREED,
    MEETUP_SCHEDULED,
    COMPLETED,
    CANCELLED
}
