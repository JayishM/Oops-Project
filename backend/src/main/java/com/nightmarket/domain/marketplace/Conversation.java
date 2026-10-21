package com.nightmarket.domain.marketplace;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Domain entity representing a buyer-seller conversation thread for a specific listing.
 */
@Document(collection = "conversations")
public class Conversation {

    @Id
    private String id;

    @Indexed
    private String listingId;
    private String listingTitle;
    private String listingImageUrl;
    private BigDecimal listingPrice;

    @Indexed
    private String sellerId;
    private String sellerName;

    @Indexed
    private String buyerId;
    private String buyerName;

    private String lastMessage;
    private LocalDateTime lastMessageTime;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Conversation() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.lastMessageTime = LocalDateTime.now();
    }

    public Conversation(String listingId, String listingTitle, String listingImageUrl,
                        BigDecimal listingPrice, String sellerId, String sellerName,
                        String buyerId, String buyerName) {
        this();
        this.listingId = listingId;
        this.listingTitle = listingTitle;
        this.listingImageUrl = listingImageUrl;
        this.listingPrice = listingPrice;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
    }

    // Getters and Setters
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

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
        this.lastMessageTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(LocalDateTime lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
