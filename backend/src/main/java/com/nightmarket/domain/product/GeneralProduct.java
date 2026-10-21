package com.nightmarket.domain.product;

import com.nightmarket.domain.marketplace.Category;
import com.nightmarket.exception.DomainValidationException;

import java.util.Arrays;
import java.util.List;

/** Concrete product for general/miscellaneous campus items. */
public class GeneralProduct extends Product {

    private String itemTypeNotes;

    public GeneralProduct() {
        super();
        setCategory(Category.OTHER);
    }

    @Override
    public String getCategorySpecificSummary() {
        return String.format("%s item: %s",
                getCategory() != null ? getCategory().getDisplayName() : "General",
                getTitle());
    }

    @Override
    public List<String> getSafetyChecklist() {
        return Arrays.asList(
                "Inspect item in a well-lit campus common area",
                "Confirm agreed price before payment",
                "Meet in public campus zones (library, cafeteria, student center)"
        );
    }

    @Override
    public void validateCategoryDetails() throws DomainValidationException {
        // No extra rules for general items
    }

    public String getItemTypeNotes() { return itemTypeNotes; }
    public void setItemTypeNotes(String v) { this.itemTypeNotes = v; }
}
