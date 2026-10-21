package com.nightmarket.app.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Android model representing a conversation summary returned by the backend.
 */
public class ConversationResponse implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("listingId")
    private String listingId;

    @SerializedName("listingTitle")
    private String listingTitle;

    @SerializedName("listingImageUrl")
    private String listingImageUrl;

    @SerializedName("listingPrice")
    private BigDecimal listingPrice;

    @SerializedName("otherParticipantId")
    private String otherParticipantId;

    @SerializedName("otherParticipantName")
    private String otherParticipantName;

    @SerializedName("lastMessage")
    private String lastMessage;

    @SerializedName("lastMessageTime")
    private String lastMessageTime;

    @SerializedName("unreadCount")
    private int unreadCount;

    public ConversationResponse() {}

    public String getId() {
        return id != null ? id : "";
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getListingId() {
        return listingId != null ? listingId : "";
    }

    public void setListingId(String listingId) {
        this.listingId = listingId;
    }

    public String getListingTitle() {
        return listingTitle != null ? listingTitle : "";
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
        return listingPrice != null ? listingPrice : BigDecimal.ZERO;
    }

    public void setListingPrice(BigDecimal listingPrice) {
        this.listingPrice = listingPrice;
    }

    public String getOtherParticipantId() {
        return otherParticipantId != null ? otherParticipantId : "";
    }

    public void setOtherParticipantId(String otherParticipantId) {
        this.otherParticipantId = otherParticipantId;
    }

    public String getOtherParticipantName() {
        return otherParticipantName != null ? otherParticipantName : "Seller";
    }

    public void setOtherParticipantName(String otherParticipantName) {
        this.otherParticipantName = otherParticipantName;
    }

    public String getLastMessage() {
        return lastMessage != null ? lastMessage : "";
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(String lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
}
