package com.nightmarket.controller;

import com.nightmarket.dto.request.SendMessageRequest;
import com.nightmarket.dto.response.ConversationResponse;
import com.nightmarket.dto.response.MessageResponse;
import com.nightmarket.service.MessageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for buyer-seller conversations and messaging.
 *
 * Endpoints:
 *   POST /api/conversations/start?listingId=...&buyerName=...   – Get or start conversation
 *   GET  /api/conversations/listing/{listingId}                 – Get or start conversation for listing
 *   GET  /api/conversations/{conversationId}/messages           – Get conversation messages
 *   POST /api/conversations/{conversationId}/messages          – Send a message
 */
@RestController
@RequestMapping("/api/conversations")
@CrossOrigin(origins = "*")
public class ConversationController {

    private final MessageService messageService;

    public ConversationController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/start")
    public ResponseEntity<ConversationResponse> startConversation(
            @RequestParam String listingId,
            @RequestParam(defaultValue = "demo-buyer") String buyerId,
            @RequestParam(defaultValue = "Student Buyer") String buyerName) {
        ConversationResponse response = messageService.startOrGetConversation(listingId, buyerId, buyerName);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<ConversationResponse> getConversationForListing(
            @PathVariable String listingId,
            @RequestParam(defaultValue = "demo-buyer") String buyerId,
            @RequestParam(defaultValue = "Student Buyer") String buyerName) {
        ConversationResponse response = messageService.startOrGetConversation(listingId, buyerId, buyerName);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "demo-buyer") String currentUserId) {
        List<MessageResponse> messages = messageService.getMessages(conversationId, currentUserId);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "demo-buyer") String senderId,
            @RequestParam(defaultValue = "Student Buyer") String senderName,
            @Valid @RequestBody SendMessageRequest request) {
        MessageResponse response = messageService.sendMessage(conversationId, senderId, senderName, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
