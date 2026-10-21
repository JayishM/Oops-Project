package com.nightmarket.dto.request;

import com.nightmarket.domain.marketplace.Category;
import com.nightmarket.domain.marketplace.Condition;
import com.nightmarket.domain.marketplace.ListingStatus;

import java.math.BigDecimal;
import java.util.List;

public class UpdateListingRequest {

    private String title;
    private String description;
    private BigDecimal price;
    private Category category;
    private Condition condition;
    private List<String> imageUrls;
    private String meetupArea;
    private Boolean negotiable;
    private ListingStatus status;

    public UpdateListingRequest() {}

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
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getMeetupArea() {
        return meetupArea;
    }

    public void setMeetupArea(String meetupArea) {
        this.meetupArea = meetupArea;
    }

    public Boolean getNegotiable() {
        return negotiable;
    }

    public void setNegotiable(Boolean negotiable) {
        this.negotiable = negotiable;
    }

    public ListingStatus getStatus() {
        return status;
    }

    public void setStatus(ListingStatus status) {
        this.status = status;
    }
}
