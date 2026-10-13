package com.nightmarket.domain.product;

import com.nightmarket.domain.marketplace.Category;
import com.nightmarket.domain.marketplace.Condition;
import com.nightmarket.exception.DomainValidationException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Specialized product subclass for sports equipment, cycles, and fitness gear.
 */
public class SportsFitness extends Product {

    private String sportType;
    private double equipmentWeightKg;

    public SportsFitness() {
        super();
        setCategory(Category.SPORTS_FITNESS);
    }

    public SportsFitness(String title, String description, BigDecimal price, Condition condition,
                         String sellerId, String sellerName, String sellerCampus, String meetupArea,
                         boolean negotiable, String sportType, double equipmentWeightKg) {
        super(title, description, price, Category.SPORTS_FITNESS, condition, sellerId, sellerName,
                sellerCampus, meetupArea, negotiable);
        this.sportType = sportType;
        this.equipmentWeightKg = equipmentWeightKg;
    }

    @Override
    public String getCategorySpecificSummary() {
        return String.format("Sports Gear [%s] (Weight: %.1f kg)",
                sportType != null ? sportType : "General Sport", equipmentWeightKg);
    }

    @Override
    public List<String> getSafetyChecklist() {
        return Arrays.asList(
                "Test cycle brakes, chain tension, and tire inflation",
                "Inspect gym gear for structural cracks or loose bolts",
                "Ensure helmet/safety protection meets certified standards"
        );
    }

    @Override
    public void validateCategoryDetails() throws DomainValidationException {
        if (equipmentWeightKg < 0) {
            throw new DomainValidationException("Weight cannot be negative.");
        }
    }

    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public double getEquipmentWeightKg() {
        return equipmentWeightKg;
    }

    public void setEquipmentWeightKg(double equipmentWeightKg) {
        this.equipmentWeightKg = equipmentWeightKg;
    }
}
