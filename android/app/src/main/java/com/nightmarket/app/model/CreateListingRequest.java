package com.nightmarket.app.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Request payload for creating a new listing on NightMarket.
 */
public class CreateListingRequest {
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

    @SerializedName("imageUrls")
    private List<String> imageUrls = new ArrayList<>();

    @SerializedName("sellerName")
    private String sellerName;

    @SerializedName("sellerPhone")
    private String sellerPhone;

    @SerializedName("sellerEmail")
    private String sellerEmail;

    @SerializedName("meetupArea")
    private String meetupArea;

    @SerializedName("negotiable")
    private boolean negotiable;

    // Optional category specific fields
    @SerializedName("brand")
    private String brand;

    @SerializedName("model")
    private String model;

    @SerializedName("author")
    private String author;

    @SerializedName("courseCode")
    private String courseCode;

    public CreateListingRequest() {}

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getCondition() { return condition; }
    public void setCondition(String condition) { this.condition = condition; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getSellerPhone() { return sellerPhone; }
    public void setSellerPhone(String sellerPhone) { this.sellerPhone = sellerPhone; }

    public String getSellerEmail() { return sellerEmail; }
    public void setSellerEmail(String sellerEmail) { this.sellerEmail = sellerEmail; }

    public String getMeetupArea() { return meetupArea; }
    public void setMeetupArea(String meetupArea) { this.meetupArea = meetupArea; }

    public boolean isNegotiable() { return negotiable; }
    public void setNegotiable(boolean negotiable) { this.negotiable = negotiable; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
}
