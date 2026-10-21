package com.nightmarket.app.model;

import com.google.gson.Gson;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ListingModelTest {

    private final Gson gson = new Gson();

    @Test
    public void testListingDeserialization() {
        String json = "{\n" +
                "  \"id\": \"test-id-123\",\n" +
                "  \"title\": \"Sony WH-CH720N\",\n" +
                "  \"description\": \"Noise cancelling headphones\",\n" +
                "  \"price\": 4200,\n" +
                "  \"category\": \"ELECTRONICS\",\n" +
                "  \"condition\": \"LIKE_NEW\",\n" +
                "  \"sellerName\": \"Aarav Sharma\",\n" +
                "  \"sellerPhone\": \"+91 9876543210\",\n" +
                "  \"status\": \"ACTIVE\",\n" +
                "  \"negotiable\": true,\n" +
                "  \"viewCount\": 5,\n" +
                "  \"owner\": true\n" +
                "}";

        Listing item = gson.fromJson(json, Listing.class);

        assertNotNull(item);
        assertEquals("test-id-123", item.getId());
        assertEquals("Sony WH-CH720N", item.getTitle());
        assertEquals(new BigDecimal("4200"), item.getPrice());
        assertEquals("ELECTRONICS", item.getCategory());
        assertEquals("Electronics", item.getCategoryDisplayName());
        assertEquals("Like new", item.getFormattedCondition());
        assertEquals("₹4200", item.getFormattedPrice());
        assertEquals("AS", item.getSellerInitials());
        assertTrue(item.isNegotiable());
        assertTrue(item.isOwner());
        assertFalse(item.isSold());
    }

    @Test
    public void testFreeItemFormatting() {
        Listing freeItem = new Listing();
        freeItem.setTitle("Course Notes");
        freeItem.setPrice(BigDecimal.ZERO);

        assertEquals("FREE", freeItem.getFormattedPrice());
    }

    @Test
    public void testSellerInitials() {
        Listing singleName = new Listing();
        singleName.setSellerName("Aarav");
        assertEquals("A", singleName.getSellerInitials());

        Listing multiName = new Listing();
        multiName.setSellerName("Priya Sharma");
        assertEquals("PS", multiName.getSellerInitials());

        Listing nullName = new Listing();
        assertEquals("NM", nullName.getSellerInitials());
    }
}
