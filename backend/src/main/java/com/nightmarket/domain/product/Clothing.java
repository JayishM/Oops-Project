package com.nightmarket.domain.product;

import com.nightmarket.domain.marketplace.Category;
import com.nightmarket.domain.marketplace.Condition;
import com.nightmarket.exception.DomainValidationException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Specialized product subclass for student apparel and footwear.
 */
public class Clothing extends Product {

    private String size;
    private String genderFit;
    private String brand;
    private String material;

    public Clothing() {
        super();
        setCategory(Category.CLOTHING);
    }

    public Clothing(String title, String description, BigDecimal price, Condition condition,
                    String sellerId, String sellerName, String sellerCampus, String meetupArea,
                    boolean negotiable, String size, String genderFit, String brand, String material) {
        super(title, description, price, Category.CLOTHING, condition, sellerId, sellerName,
                sellerCampus, meetupArea, negotiable);
        this.size = size;
        this.genderFit = genderFit;
        this.brand = brand;
        this.material = material;
    }

    @Override
    public String getCategorySpecificSummary() {
        return String.format("Apparel by %s (Size: %s, Fit: %s, Fabric: %s)",
                brand != null ? brand : "Unbranded",
                size != null ? size : "Standard",
                genderFit != null ? genderFit : "Unisex",
                material != null ? material : "Cotton");
    }

    @Override
    public List<String> getSafetyChecklist() {
        return Arrays.asList(
                "Inspect seams, zippers, and buttons for damage",
                "Verify sizing fit during daylight or well-lit meetup area",
                "Items should be washed prior to exchange"
        );
    }

    @Override
    public void validateCategoryDetails() throws DomainValidationException {
        if (size == null || size.trim().isEmpty()) {
            throw new DomainValidationException("Apparel size is required.");
        }
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getGenderFit() {
        return genderFit;
    }

    public void setGenderFit(String genderFit) {
        this.genderFit = genderFit;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }
}
