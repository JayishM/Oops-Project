package com.nightmarket.app.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Android model representing an individual chat message in a conversation.
 */
public class MessageResponse implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("conversationId")
    private String conversationId;

    @SerializedName("senderId")
    private String senderId;

    @SerializedName("senderName")
    private String senderName;

    @SerializedName("content")
    private String content;

    @SerializedName("type")
    private String type;

    @SerializedName("metadataOfferAmount")
    private BigDecimal metadataOfferAmount;

    @SerializedName("metadataOfferId")
    private String metadataOfferId;

    @SerializedName("isSentByCurrentUser")
    private boolean sentByCurrentUser;

    @SerializedName("isRead")
    private boolean read;

    @SerializedName("sentAt")
    private String sentAt;

    public MessageResponse() {}

    public String getId() {
        return id != null ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId != null ? conversationId : "";
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSenderId() {
        return senderId != null ? senderId : "";
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName != null ? senderName : "User";
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content != null ? content : "";
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type != null ? type : "TEXT";
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getMetadataOfferAmount() {
        return metadataOfferAmount;
    }

    public void setMetadataOfferAmount(BigDecimal metadataOfferAmount) {
        this.metadataOfferAmount = metadataOfferAmount;
    }

    public String getMetadataOfferId() {
        return metadataOfferId;
    }

    public void setMetadataOfferId(String metadataOfferId) {
        this.metadataOfferId = metadataOfferId;
    }

    public boolean isSentByCurrentUser() {
        return sentByCurrentUser;
    }

    public void setSentByCurrentUser(boolean sentByCurrentUser) {
        this.sentByCurrentUser = sentByCurrentUser;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getSentAt() {
        return sentAt != null ? sentAt : "";
    }

    public void setSentAt(String sentAt) {
        this.sentAt = sentAt;
    }

    public String getFormattedTime() {
        if (sentAt == null || sentAt.isEmpty()) return "";
        try {
            // E.g. 2026-10-21T18:30:00 -> extract hh:mm
            if (sentAt.contains("T")) {
                String timePart = sentAt.split("T")[1];
                String[] parts = timePart.split(":");
                if (parts.length >= 2) {
                    int hour = Integer.parseInt(parts[0]);
                    int min = Integer.parseInt(parts[1]);
                    String ampm = hour >= 12 ? "PM" : "AM";
                    int displayHour = hour % 12;
                    if (displayHour == 0) displayHour = 12;
                    return String.format(java.util.Locale.getDefault(), "%d:%02d %s", displayHour, min, ampm);
                }
            }
        } catch (Exception ignored) {}
        return "";
    }
}
