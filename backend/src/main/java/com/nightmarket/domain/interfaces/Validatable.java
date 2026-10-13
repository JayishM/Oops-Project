package com.nightmarket.domain.interfaces;

import com.nightmarket.exception.DomainValidationException;

/**
 * Contract for domain models that perform internal business invariant validation.
 * Demonstrates Abstraction and Encapsulation.
 */
public interface Validatable {
    /**
     * Validates that domain invariants and business rules are satisfied.
     * @throws DomainValidationException if any business invariant is violated.
     */
    void validate() throws DomainValidationException;
}
