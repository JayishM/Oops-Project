package com.nightmarket.domain.interfaces;

import com.nightmarket.domain.marketplace.Notification;

/**
 * Interface contract for notification delivery.
 * Demonstrates Abstraction and Dependency Inversion Principle.
 */
public interface NotificationSender {
    /**
     * Dispatches a notification to recipient.
     */
    void send(Notification notification);
}
