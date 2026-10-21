package com.nightmarket.app.model;

import com.google.gson.Gson;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class ConversationModelTest {

    private final Gson gson = new Gson();

    @Test
    public void testConversationResponseDeserialization() {
        String json = "{\n" +
                "  \"id\": \"conv-999\",\n" +
                "  \"listingId\": \"list-123\",\n" +
                "  \"listingTitle\": \"Calculus 8th Edition\",\n" +
                "  \"listingImageUrl\": \"/api/images/img-555\",\n" +
                "  \"listingPrice\": 450,\n" +
                "  \"otherParticipantId\": \"seller-456\",\n" +
                "  \"otherParticipantName\": \"Priya Sharma\",\n" +
                "  \"lastMessage\": \"Is this still available?\",\n" +
                "  \"lastMessageTime\": \"2026-10-21T10:00:00Z\",\n" +
                "  \"unreadCount\": 1\n" +
                "}";

        ConversationResponse conv = gson.fromJson(json, ConversationResponse.class);

        assertNotNull(conv);
        assertEquals("conv-999", conv.getId());
        assertEquals("list-123", conv.getListingId());
        assertEquals("Calculus 8th Edition", conv.getListingTitle());
        assertEquals("/api/images/img-555", conv.getListingImageUrl());
        assertEquals(new BigDecimal("450"), conv.getListingPrice());
        assertEquals("seller-456", conv.getOtherParticipantId());
        assertEquals("Priya Sharma", conv.getOtherParticipantName());
        assertEquals("Is this still available?", conv.getLastMessage());
        assertEquals("2026-10-21T10:00:00Z", conv.getLastMessageTime());
        assertEquals(1, conv.getUnreadCount());
    }

    @Test
    public void testMessageResponseDeserialization() {
        String json = "{\n" +
                "  \"id\": \"msg-001\",\n" +
                "  \"conversationId\": \"conv-999\",\n" +
                "  \"senderId\": \"demo-buyer\",\n" +
                "  \"senderName\": \"Demo Buyer\",\n" +
                "  \"content\": \"Hi, can I pick it up at the North Gate?\",\n" +
                "  \"type\": \"TEXT\",\n" +
                "  \"isSentByCurrentUser\": true,\n" +
                "  \"isRead\": false,\n" +
                "  \"sentAt\": \"2026-10-21T10:05:00Z\"\n" +
                "}";

        MessageResponse msg = gson.fromJson(json, MessageResponse.class);

        assertNotNull(msg);
        assertEquals("msg-001", msg.getId());
        assertEquals("conv-999", msg.getConversationId());
        assertEquals("demo-buyer", msg.getSenderId());
        assertEquals("Demo Buyer", msg.getSenderName());
        assertEquals("Hi, can I pick it up at the North Gate?", msg.getContent());
        assertEquals("TEXT", msg.getType());
        assertTrue(msg.isSentByCurrentUser());
        assertFalse(msg.isRead());
        assertEquals("2026-10-21T10:05:00Z", msg.getSentAt());
    }

    @Test
    public void testSendMessageRequestSerialization() {
        SendMessageRequest request = new SendMessageRequest("Is this available?");
        String json = gson.toJson(request);

        assertTrue(json.contains("\"content\":\"Is this available?\""));
        assertEquals("Is this available?", request.getContent());
    }
}
