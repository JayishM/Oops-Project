package com.nightmarket.app.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Android client model matching the NightMarket backend's Listing JSON representations.
 */
public class Listing implements Serializable {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("price")
    private BigDecimal price;

    @SerializedName("category")
    private String category;

    @SerializedName("condition")
    private String condition;

    @SerializedName("primaryImageUrl")
    private String primaryImageUrl;

    @SerializedName("imageUrls")
    private List<String> imageUrls;

    @SerializedName("sellerId")
    private String sellerId;

    @SerializedName("sellerName")
    private String sellerName;

    @SerializedName("sellerCampus")
    private String sellerCampus;

    @SerializedName("sellerPhone")
    private String sellerPhone;

    @SerializedName("sellerEmail")
    private String sellerEmail;

    @SerializedName("meetupArea")
    private String meetupArea;

    @SerializedName("status")
    private String status;

    @SerializedName("negotiable")
    private boolean negotiable;

    @SerializedName("viewCount")
    private int viewCount;

    @SerializedName("favoriteCount")
    private int favoriteCount;

    @SerializedName("createdAt")
    private String createdAt;

    @SerializedName("categorySummary")
    private String categorySummary;

    @SerializedName("safetyChecklist")
    private List<String> safetyChecklist;

    @SerializedName("owner")
    private boolean owner;

    public Listing() {}

    // Getters and Setters
    public String getId() { return id != null ? id : ""; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title != null ? title : ""; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description != null ? description : ""; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price != null ? price : BigDecimal.ZERO; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category != null ? category : "OTHER"; }
    public void setCategory(String category) { this.category = category; }

    public String getCondition() { return condition != null ? condition : "GOOD"; }
    public void setCondition(String condition) { this.condition = condition; }

    public String getPrimaryImageUrl() { return primaryImageUrl; }
    public void setPrimaryImageUrl(String primaryImageUrl) { this.primaryImageUrl = primaryImageUrl; }

    public List<String> getImageUrls() { return imageUrls != null ? imageUrls : new ArrayList<>(); }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName != null && !sellerName.isEmpty() ? sellerName : "Student Seller"; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getSellerCampus() { return sellerCampus != null ? sellerCampus : "Campus"; }
    public void setSellerCampus(String sellerCampus) { this.sellerCampus = sellerCampus; }

    public String getSellerPhone() { return sellerPhone != null ? sellerPhone : ""; }
    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }

    public String getSellerEmail() { return sellerEmail != null ? sellerEmail : ""; }
    public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }

    public String getMeetupArea() { return meetupArea != null && !meetupArea.isEmpty() ? meetupArea : "Campus meetup spot"; }
    public void setMeetupArea(String meetupArea) { this.meetupArea = meetupArea; }

    public String getStatus() { return status != null ? status : "ACTIVE"; }
    public void setStatus(String status) { this.status = status; }

    public boolean isNegotiable() { return negotiable; }
    public void setNegotiable(boolean negotiable) { this.negotiable = negotiable; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public int getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getCategorySummary() { return categorySummary; }
    public void setCategorySummary(String categorySummary) { this.categorySummary = categorySummary; }

    public List<String> getSafetyChecklist() { return safetyChecklist != null ? safetyChecklist : new ArrayList<>(); }
    public void setSafetyChecklist(List<String> safetyChecklist) { this.safetyChecklist = safetyChecklist; }

    public boolean isOwner() { return owner; }
    public void setOwner(boolean owner) { this.owner = owner; }

    public boolean isSold() { return "SOLD".equalsIgnoreCase(status); }

    public String getFormattedPrice() {
        if (price == null || price.compareTo(BigDecimal.ZERO) == 0) {
            return "FREE";
        }
        return "₹" + price.stripTrailingZeros().toPlainString();
    }

    public String getFormattedCondition() {
        if (condition == null) return "Good";
        switch (condition.toUpperCase()) {
            case "BRAND_NEW": return "Brand new";
            case "LIKE_NEW": return "Like new";
            case "FAIR": return "Fair";
            case "GOOD":
            default:
                return "Good";
        }
    }

    public String getCategoryDisplayName() {
        if (category == null) return "General";
        switch (category.toUpperCase()) {
            case "TEXTBOOKS": return "Textbooks";
            case "ELECTRONICS": return "Electronics";
            case "CLOTHING": return "Fashion";
            case "HOSTEL_ESSENTIALS": return "Room Essentials";
            case "SPORTS_FITNESS": return "Sports";
            case "FREE_STUFF": return "Free";
            default: return "Other";
        }
    }

    public String getSellerInitials() {
        if (sellerName == null || sellerName.trim().isEmpty()) return "NM";
        String[] parts = sellerName.trim().split("\\s+");
        if (parts.length >= 2) {
            return ("" + parts[0].charAt(0) + parts[1].charAt(0)).toUpperCase();
        }
        return ("" + sellerName.trim().charAt(0)).toUpperCase();
    }
}
