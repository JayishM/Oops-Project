package com.nightmarket.domain.marketplace;

import java.time.LocalDateTime;
import java.util.UUID;

/** Domain model for a notification delivered to a campus marketplace user. */
public class Notification {
    private String id;
    private String recipientId;
    private String title;
    private String message;
    private NotificationType type;
    private String referenceId;
    private boolean read;
    private LocalDateTime createdAt;

    public Notification() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String recipientId, String title, String message,
                        NotificationType type, String referenceId) {
        this();
        this.recipientId = recipientId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.referenceId = referenceId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    public String getReferenceId() { return referenceId; }
    public void setReferenceId(String referenceId) { this.referenceId = referenceId; }
    public boolean isRead() { return read; }
    public void setRead(boolean read) { this.read = read; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
