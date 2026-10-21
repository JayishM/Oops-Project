package com.nightmarket.domain.marketplace;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain entity representing an individual chat message in a conversation.
 */
@Document(collection = "messages")
public class ChatMessage {

    @Id
    private String id;

    @Indexed
    private String conversationId;
    private String listingId;

    private String senderId;
    private String senderName;
    private String receiverId;

    private String content;
    private MessageType type = MessageType.TEXT;

    private BigDecimal metadataOfferAmount;
    private String metadataOfferId;

    private boolean read;
    private LocalDateTime sentAt;

    public ChatMessage() {
        this.sentAt = LocalDateTime.now();
        this.type = MessageType.TEXT;
    }

    public ChatMessage(String conversationId, String listingId, String senderId,
                       String senderName, String receiverId, String content) {
        this();
        this.conversationId = conversationId;
        this.listingId = listingId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.content = content;
    }

    // Getters and Setters
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

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
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

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
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

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}
