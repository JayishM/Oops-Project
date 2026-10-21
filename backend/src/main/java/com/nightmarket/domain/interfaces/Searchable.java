package com.nightmarket.domain.interfaces;

import java.util.List;

/**
 * Interface representing items that can be searched using keyword queries.
 * Demonstrates Abstraction and Interface Segregation.
 */
public interface Searchable {
    /**
     * Checks if this item matches the specified search query tokens.
     */
    boolean matchesSearchCriteria(String query);

    /**
     * Extracts searchable keywords for indexing and filtering.
     */
    List<String> extractKeywords();
}
