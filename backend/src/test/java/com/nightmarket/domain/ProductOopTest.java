package com.nightmarket.domain;

import com.nightmarket.domain.marketplace.Category;
import com.nightmarket.domain.marketplace.Condition;
import com.nightmarket.domain.marketplace.ListingStatus;
import com.nightmarket.domain.product.Electronics;
import com.nightmarket.domain.product.HostelEssential;
import com.nightmarket.domain.product.Product;
import com.nightmarket.domain.product.Textbook;
import com.nightmarket.exception.DomainValidationException;
import com.nightmarket.exception.InvalidOfferException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("OOP Concepts and Domain Invariants Test Suite")
public class ProductOopTest {

    @Test
    @DisplayName("Polymorphism: getCategorySpecificSummary() returns subclass-specific details")
    void testPolymorphismCategorySummary() {
        Textbook tb = new Textbook();
        tb.setAuthor("Silberschatz");
        tb.setCourseCode("CS202");
        tb.setEdition("10th");

        Electronics el = new Electronics();
        el.setBrand("Sony");
        el.setModel("WH-CH720N");
        el.setWarrantyMonths(6);
        el.setWorkingBattery(true);

        Product p1 = tb; // Polymorphic reference
        Product p2 = el; // Polymorphic reference

        assertTrue(p1.getCategorySpecificSummary().contains("Silberschatz"), "Textbook summary must include author");
        assertTrue(p1.getCategorySpecificSummary().contains("CS202"), "Textbook summary must include course code");

        assertTrue(p2.getCategorySpecificSummary().contains("Sony"), "Electronics summary must include brand");
        assertTrue(p2.getCategorySpecificSummary().contains("WH-CH720N"), "Electronics summary must include model");
    }

    @Test
    @DisplayName("Polymorphism: getSafetyChecklist() returns category-specific buyer checklist")
    void testPolymorphicSafetyChecklist() {
        Product textbook = new Textbook();
        Product electronics = new Electronics();

        List<String> tbList = textbook.getSafetyChecklist();
        List<String> elList = electronics.getSafetyChecklist();

        assertNotNull(tbList);
        assertNotNull(elList);
        assertNotEquals(tbList, elList, "Safety checklist must be polymorphic per product subclass");
        assertTrue(tbList.stream().anyMatch(item -> item.toLowerCase().contains("page") || item.toLowerCase().contains("edition") || item.toLowerCase().contains("isbn")));
        assertTrue(elList.stream().anyMatch(item -> item.toLowerCase().contains("power") || item.toLowerCase().contains("battery") || item.toLowerCase().contains("charging")));
    }

    @Test
    @DisplayName("Encapsulation: markSold() transitions product state to SOLD")
    void testEncapsulatedStateTransition() {
        Product el = new Electronics();
        assertEquals(ListingStatus.ACTIVE, el.getStatus(), "Initial status must be ACTIVE");

        el.markSold();
        assertEquals(ListingStatus.SOLD, el.getStatus(), "Status must transition to SOLD");
    }

    @Test
    @DisplayName("Encapsulation: View counts and favorite counts mutate through guarded methods")
    void testViewAndFavoriteEncapsulation() {
        Product p = new HostelEssential();
        assertEquals(0, p.getViewCount());
        assertEquals(0, p.getFavoriteCount());

        p.incrementViewCount();
        p.incrementViewCount();
        assertEquals(2, p.getViewCount());

        p.incrementFavoriteCount();
        assertEquals(1, p.getFavoriteCount());

        p.decrementFavoriteCount();
        assertEquals(0, p.getFavoriteCount());

        // Decrement below 0 should be guarded
        p.decrementFavoriteCount();
        assertEquals(0, p.getFavoriteCount(), "Favorite count must not fall below 0");
    }

    @Test
    @DisplayName("Abstraction & Validatable: validate() enforces core product invariants")
    void testValidationContract() {
        Textbook tb = new Textbook();
        tb.setTitle("Algorithm Design");
        tb.setPrice(new BigDecimal("200"));
        // Missing sellerId
        assertThrows(DomainValidationException.class, tb::validate, "Missing sellerId must fail validation");

        tb.setSellerId("student-123");
        // Missing condition
        assertThrows(DomainValidationException.class, tb::validate, "Missing condition must fail validation");

        tb.setCondition(Condition.GOOD);
        assertDoesNotThrow(tb::validate, "Fully valid product must pass validation");
    }

    @Test
    @DisplayName("Offerable & Negotiable: validateOfferAmount() and getMinimumAcceptablePrice() enforce contracts")
    void testOfferableAndNegotiableContracts() {
        Product p = new Electronics();
        p.setPrice(new BigDecimal("1000"));
        p.setNegotiable(true);

        assertTrue(p.isPriceNegotiable());
        assertEquals(new BigDecimal("700.00"), p.getMinimumAcceptablePrice());

        // Offer validation: MIN_OFFER_RATIO is 30% (300)
        assertDoesNotThrow(() -> p.validateOfferAmount(new BigDecimal("500")), "500 offer is valid");
        assertThrows(InvalidOfferException.class, () -> p.validateOfferAmount(new BigDecimal("200")), "200 is below 30% min and must fail");
    }
}
