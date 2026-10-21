package com.nightmarket.dto.response;

import com.nightmarket.domain.marketplace.MessageType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MessageResponse {

    private String id;
    private String conversationId;
    private String senderId;
    private String senderName;
    private String content;
    private MessageType type;
    private BigDecimal metadataOfferAmount;
    private String metadataOfferId;
    private boolean isSentByCurrentUser;
    private boolean isRead;
    private LocalDateTime sentAt;

    public MessageResponse() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
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
        return isSentByCurrentUser;
    }

    public void setSentByCurrentUser(boolean sentByCurrentUser) {
        isSentByCurrentUser = sentByCurrentUser;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
