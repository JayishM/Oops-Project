package com.nightmarket.domain.product;

import com.nightmarket.domain.marketplace.Category;
import com.nightmarket.domain.marketplace.Condition;
import com.nightmarket.exception.DomainValidationException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Specialized product subclass for hostel and dorm living essentials.
 * Demonstrates Polymorphism and Category-Specific Invariant Checks.
 */
public class HostelEssential extends Product {

    private String itemSubtype; // e.g. "Kettle", "Table Lamp", "Mattress", "Drying Rack"
    private int durationUsedMonths;
    private boolean bulky;

    public HostelEssential() {
        super();
        setCategory(Category.HOSTEL_ESSENTIALS);
    }

    public HostelEssential(String title, String description, BigDecimal price, Condition condition,
                           String sellerId, String sellerName, String sellerCampus, String meetupArea,
                           boolean negotiable, String itemSubtype, int durationUsedMonths, boolean bulky) {
        super(title, description, price, Category.HOSTEL_ESSENTIALS, condition, sellerId, sellerName,
                sellerCampus, meetupArea, negotiable);
        this.itemSubtype = itemSubtype;
        this.durationUsedMonths = durationUsedMonths;
        this.bulky = bulky;
    }

    @Override
    public String getCategorySpecificSummary() {
        return String.format("Hostel Item [%s] (Used %d mos, Transport: %s)",
                itemSubtype != null ? itemSubtype : "Essential",
                durationUsedMonths,
                bulky ? "Bulky / Requires Cart or Helper" : "Portable / Fits in Backpack");
    }

    @Override
    public List<String> getSafetyChecklist() {
        return Arrays.asList(
                "Inspect electrical cables/heating elements for fraying",
                "Coordinate meeting near hostel lobby if item is bulky",
                "Sanitize pre-owned hostel appliances before dorm usage"
        );
    }

    @Override
    public void validateCategoryDetails() throws DomainValidationException {
        if (durationUsedMonths < 0) {
            throw new DomainValidationException("Duration used cannot be negative.");
        }
    }

    public String getItemSubtype() {
        return itemSubtype;
    }

    public void setItemSubtype(String itemSubtype) {
        this.itemSubtype = itemSubtype;
    }

    public int getDurationUsedMonths() {
        return durationUsedMonths;
    }

    public void setDurationUsedMonths(int durationUsedMonths) {
        this.durationUsedMonths = durationUsedMonths;
    }

    public boolean isBulky() {
        return bulky;
    }

    public void setBulky(boolean bulky) {
        this.bulky = bulky;
    }
}
