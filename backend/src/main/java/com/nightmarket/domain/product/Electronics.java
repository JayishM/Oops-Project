package com.nightmarket.domain.product;

import com.nightmarket.domain.marketplace.Category;
import com.nightmarket.domain.marketplace.Condition;
import com.nightmarket.exception.DomainValidationException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Specialized product subclass for electronic devices, calculators, and accessories.
 * Demonstrates Polymorphism and Category-Specific Behavior.
 */
public class Electronics extends Product {

    private String brand;
    private String model;
    private int warrantyMonths;
    private boolean includesAccessories;
    private boolean workingBattery;

    public Electronics() {
        super();
        setCategory(Category.ELECTRONICS);
    }

    public Electronics(String title, String description, BigDecimal price, Condition condition,
                       String sellerId, String sellerName, String sellerCampus, String meetupArea,
                       boolean negotiable, String brand, String model, int warrantyMonths,
                       boolean includesAccessories, boolean workingBattery) {
        super(title, description, price, Category.ELECTRONICS, condition, sellerId, sellerName,
                sellerCampus, meetupArea, negotiable);
        this.brand = brand;
        this.model = model;
        this.warrantyMonths = warrantyMonths;
        this.includesAccessories = includesAccessories;
        this.workingBattery = workingBattery;
    }

    @Override
    public String getCategorySpecificSummary() {
        return String.format("%s %s (Battery: %s, Accessories: %s, Warranty: %d mos)",
                brand != null ? brand : "Generic",
                model != null ? model : "Device",
                workingBattery ? "Tested & Working" : "Battery Depleted/Missing",
                includesAccessories ? "Included" : "None",
                warrantyMonths);
    }

    @Override
    public List<String> getSafetyChecklist() {
        return Arrays.asList(
                "Power on device and test basic buttons/display in person",
                "Check charging port with power bank during campus meetup",
                "Inspect screen for dead pixels, cracks, or lines"
        );
    }

    @Override
    public void validateCategoryDetails() throws DomainValidationException {
        if (warrantyMonths < 0) {
            throw new DomainValidationException("Warranty months cannot be negative.");
        }
    }

    // Getters and Setters
    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getWarrantyMonths() {
        return warrantyMonths;
    }

    public void setWarrantyMonths(int warrantyMonths) {
        this.warrantyMonths = warrantyMonths;
    }

    public boolean isIncludesAccessories() {
        return includesAccessories;
    }

    public void setIncludesAccessories(boolean includesAccessories) {
        this.includesAccessories = includesAccessories;
    }

    public boolean isWorkingBattery() {
        return workingBattery;
    }

    public void setWorkingBattery(boolean workingBattery) {
        this.workingBattery = workingBattery;
    }
}
