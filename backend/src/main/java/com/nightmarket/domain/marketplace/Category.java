package com.nightmarket.domain.marketplace;

/**
 * Standard categories available in the NightMarket campus bazaar.
 * Encapsulates category taxonomy and UI display helpers.
 */
public enum Category {
    TEXTBOOKS("Textbooks & Academics", "Used books, lab manuals, and study notes"),
    ELECTRONICS("Electronics & Gadgets", "Calculators, headphones, chargers, displays"),
    CLOTHING("Fashion & Apparel", "Hoodies, shoes, formal wear, ethnic wear"),
    HOSTEL_ESSENTIALS("Hostel & Room Essentials", "Kettles, lamps, mattresses, storage"),
    SPORTS_FITNESS("Sports & Fitness", "Cycles, racquets, gym gear, skates"),
    FREE_STUFF("Free Stuff", "Donated items given away at zero cost to students"),
    OTHER("Miscellaneous", "Items not fitting other distinct classifications");

    private final String displayName;
    private final String description;

    Category(String displayName, String description) {
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
