package com.nightmarket.repository;

import com.nightmarket.domain.marketplace.ListingStatus;
import com.nightmarket.domain.product.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * MongoDB persistence boundary for all concrete Product subclasses.
 * Spring Data stores concrete runtime type metadata so category-specific OOP
 * data is restored when a Product is read back from the listings collection.
 */
public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByStatusOrderByCreatedAtDesc(ListingStatus status);
    List<Product> findBySellerIdOrderByCreatedAtDesc(String sellerId);
}
