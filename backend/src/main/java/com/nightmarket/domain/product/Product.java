package com.nightmarket.domain.product;

import com.nightmarket.domain.interfaces.Negotiable;
import com.nightmarket.domain.interfaces.Offerable;
import com.nightmarket.domain.interfaces.Searchable;
import com.nightmarket.domain.interfaces.Validatable;
import com.nightmarket.domain.marketplace.Category;
import com.nightmarket.domain.marketplace.Condition;
import com.nightmarket.domain.marketplace.ListingStatus;
import com.nightmarket.exception.DomainValidationException;
import com.nightmarket.exception.InvalidOfferException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Abstract base class representing a marketplace product.
 *
 * OOP Concepts demonstrated:
 *   1. Abstraction  – abstract methods force subclasses to provide category-specific behaviour.
 *   2. Polymorphism – getCategorySpecificSummary() behaves differently per subclass.
 *   3. Encapsulation – status, view-count, favourite-count mutated only through guarded methods.
 *   4. Multiple interfaces – Searchable, Offerable, Negotiable, Validatable.
 */
public abstract class Product implements Validatable, Searchable, Offerable, Negotiable {

    private String id;
    private String title;
    private String description;
    private BigDecimal price;
    private Category category;
    private Condition condition;
    private List<String> imageUrls = new ArrayList<>();
    private String sellerId;
    private String sellerName;
    private String sellerCampus;
    private String meetupArea;
    private ListingStatus status = ListingStatus.ACTIVE;
    private boolean negotiable;
    private int viewCount;
    private int favoriteCount;
    private int reportCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static final BigDecimal MIN_OFFER_RATIO = new BigDecimal("0.30");

    protected Product() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ListingStatus.ACTIVE;
        this.imageUrls = new ArrayList<>();
    }

    /** Convenience constructor used by the category-specific product subclasses. */
    protected Product(String title, String description, BigDecimal price, Category category,
                      Condition condition, String sellerId, String sellerName,
                      String sellerCampus, String meetupArea, boolean negotiable) {
        this();
        this.title = title;
        this.description = description;
        this.price = price;
        this.category = category;
        this.condition = condition;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.sellerCampus = sellerCampus;
        this.meetupArea = meetupArea;
        this.negotiable = negotiable;
    }

    // ── Polymorphic Abstract Methods ─────────────────────────────────────────

    /** Returns a human-readable summary of category-specific attributes. */
    public abstract String getCategorySpecificSummary();

    /** Returns a checklist of things a buyer should verify at campus meetup. */
    public abstract List<String> getSafetyChecklist();

    /** Validates category-specific invariants (called inside validate()). */
    public abstract void validateCategoryDetails() throws DomainValidationException;

    // ── Searchable ────────────────────────────────────────────────────────────

    @Override
    public boolean matchesSearchCriteria(String query) {
        if (query == null || query.trim().isEmpty()) return true;
        String q = query.toLowerCase(Locale.ROOT).trim();
        return (title != null && title.toLowerCase(Locale.ROOT).contains(q))
                || (description != null && description.toLowerCase(Locale.ROOT).contains(q))
                || (sellerCampus != null && sellerCampus.toLowerCase(Locale.ROOT).contains(q));
    }

    @Override
    public List<String> extractKeywords() {
        List<String> kw = new ArrayList<>();
        if (title != null) kw.addAll(Arrays.asList(title.toLowerCase(Locale.ROOT).split("\\s+")));
        if (category != null) kw.add(category.name().toLowerCase(Locale.ROOT));
        return Collections.unmodifiableList(kw);
    }

    // ── Offerable ─────────────────────────────────────────────────────────────

    @Override
    public boolean canReceiveOffers() {
        return this.status == ListingStatus.ACTIVE;
    }

    @Override
    public void validateOfferAmount(BigDecimal offerAmount) throws InvalidOfferException {
        if (!canReceiveOffers())
            throw new InvalidOfferException("Cannot make an offer on an inactive or sold item.");
        if (offerAmount == null || offerAmount.compareTo(BigDecimal.ZERO) <= 0)
            throw new InvalidOfferException("Offer amount must be greater than zero.");
        if (price.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal min = price.multiply(MIN_OFFER_RATIO);
            if (offerAmount.compareTo(min) < 0)
                throw new InvalidOfferException("Offer ₹" + offerAmount + " is too low (min ₹" + min + ").");
        }
    }

    // ── Negotiable ────────────────────────────────────────────────────────────

    @Override
    public boolean isPriceNegotiable() { return this.negotiable; }

    @Override
    public BigDecimal getMinimumAcceptablePrice() {
        return this.negotiable ? this.price.multiply(new BigDecimal("0.70")) : this.price;
    }

    // ── Validatable ───────────────────────────────────────────────────────────

    @Override
    public void validate() throws DomainValidationException {
        if (title == null || title.trim().length() < 3)
            throw new DomainValidationException("Title must be at least 3 characters.");
        if (price == null || price.compareTo(BigDecimal.ZERO) < 0)
            throw new DomainValidationException("Price cannot be negative.");
        if (sellerId == null || sellerId.trim().isEmpty())
            throw new DomainValidationException("Listing must have a seller ID.");
        if (category == null)
            throw new DomainValidationException("Category must be selected.");
        if (condition == null)
            throw new DomainValidationException("Condition must be specified.");
        validateCategoryDetails();
    }

    // ── Domain Mutations ──────────────────────────────────────────────────────

    public void markSold() {
        this.status = ListingStatus.SOLD;
        this.updatedAt = LocalDateTime.now();
    }

    public void markActive() {
        this.status = ListingStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementViewCount() { this.viewCount++; }

    public void incrementFavoriteCount() { this.favoriteCount++; }

    public void decrementFavoriteCount() { if (this.favoriteCount > 0) this.favoriteCount--; }

    public void incrementReportCount() {
        this.reportCount++;
        this.updatedAt = LocalDateTime.now();
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    public String getSellerId() { return sellerId; }
    public void setSellerId(String sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public String getSellerCampus() { return sellerCampus; }
    public void setSellerCampus(String sellerCampus) { this.sellerCampus = sellerCampus; }

    public String getMeetupArea() { return meetupArea; }
    public void setMeetupArea(String meetupArea) { this.meetupArea = meetupArea; }

    public ListingStatus getStatus() { return status; }
    public void setStatus(ListingStatus status) { this.status = status; }

    public boolean isNegotiable() { return negotiable; }
    public void setNegotiable(boolean negotiable) { this.negotiable = negotiable; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public int getFavoriteCount() { return favoriteCount; }
    public void setFavoriteCount(int favoriteCount) { this.favoriteCount = favoriteCount; }

    public int getReportCount() { return reportCount; }
    public void setReportCount(int reportCount) { this.reportCount = reportCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
