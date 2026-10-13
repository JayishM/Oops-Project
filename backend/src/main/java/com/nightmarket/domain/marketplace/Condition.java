package com.nightmarket.domain.marketplace;

/**
 * Item physical condition assessment.
 * Demonstrates Java Enums with custom behavior and display methods.
 */
public enum Condition {
    BRAND_NEW("Brand New", "Unopened in original packaging"),
    LIKE_NEW("Like New", "Gently used with no visible flaws or wear"),
    GOOD("Good", "Minor signs of use but fully functional"),
    FAIR("Fair", "Visible wear and tear, fully operational");

    private final String displayName;
    private final String description;

    Condition(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }
}
