package com.nightmarket.dto.response;

import com.nightmarket.domain.marketplace.Category;
import com.nightmarket.domain.marketplace.Condition;
import com.nightmarket.domain.marketplace.ListingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ListingSummaryResponse {

    private String id;
    private String title;
    private String description;
    private BigDecimal price;
    private Category category;
    private String categoryDisplayName;
    private Condition condition;
    private String conditionDisplayName;
    private String primaryImageUrl;
    private String sellerId;
    private String sellerName;
    private String sellerCampus;
    private String meetupArea;
    private ListingStatus status;
    private boolean negotiable;
    private int viewCount;
    private int favoriteCount;
    private boolean isFavorite;
    private LocalDateTime createdAt;

    public ListingSummaryResponse() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryDisplayName = category.getDisplayName();
        }
    }

    public String getCategoryDisplayName() {
        return categoryDisplayName;
    }

    public void setCategoryDisplayName(String categoryDisplayName) {
        this.categoryDisplayName = categoryDisplayName;
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
        if (condition != null) {
            this.conditionDisplayName = condition.getDisplayName();
        }
    }

    public String getConditionDisplayName() {
        return conditionDisplayName;
    }

    public void setConditionDisplayName(String conditionDisplayName) {
        this.conditionDisplayName = conditionDisplayName;
    }

    public String getPrimaryImageUrl() {
        return primaryImageUrl;
    }

    public void setPrimaryImageUrl(String primaryImageUrl) {
        this.primaryImageUrl = primaryImageUrl;
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

    public String getSellerCampus() {
        return sellerCampus;
    }

    public void setSellerCampus(String sellerCampus) {
        this.sellerCampus = sellerCampus;
    }

    public String getMeetupArea() {
        return meetupArea;
    }

    public void setMeetupArea(String meetupArea) {
        this.meetupArea = meetupArea;
    }

    public ListingStatus getStatus() {
        return status;
    }

    public void setStatus(ListingStatus status) {
        this.status = status;
    }

    public boolean isNegotiable() {
        return negotiable;
    }

    public void setNegotiable(boolean negotiable) {
        this.negotiable = negotiable;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
