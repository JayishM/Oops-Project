package com.nightmarket.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ConversationResponse {

    private String id;
    private String listingId;
    private String listingTitle;
    private String listingImageUrl;
    private BigDecimal listingPrice;
    private String otherParticipantId;
    private String otherParticipantName;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private int unreadCount;

    public ConversationResponse() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getListingId() {
        return listingId;
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getListingTitle() {
        return listingTitle;
    }

    public void setListingTitle(String listingTitle) {
        this.listingTitle = listingTitle;
    }

    public String getListingImageUrl() {
        return listingImageUrl;
    }

    public void setListingImageUrl(String listingImageUrl) {
        this.listingImageUrl = listingImageUrl;
    }

    public BigDecimal getListingPrice() {
        return listingPrice;
    }

    public void setListingPrice(BigDecimal listingPrice) {
        this.listingPrice = listingPrice;
    }

    public String getOtherParticipantId() {
        return otherParticipantId;
    }

    public void setOtherParticipantId(String otherParticipantId) {
        this.otherParticipantId = otherParticipantId;
    }

    public String getOtherParticipantName() {
        return otherParticipantName;
    }

    public void setOtherParticipantName(String otherParticipantName) {
        this.otherParticipantName = otherParticipantName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
