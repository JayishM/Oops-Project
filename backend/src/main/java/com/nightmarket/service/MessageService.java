package com.nightmarket.service;

import com.nightmarket.domain.marketplace.ChatMessage;
import com.nightmarket.domain.marketplace.Conversation;
import com.nightmarket.domain.marketplace.MessageType;
import com.nightmarket.domain.product.Product;
import com.nightmarket.dto.request.SendMessageRequest;
import com.nightmarket.dto.response.ConversationResponse;
import com.nightmarket.dto.response.MessageResponse;
import com.nightmarket.exception.ListingNotFoundException;
import com.nightmarket.exception.ResourceNotFoundException;
import com.nightmarket.repository.ChatMessageRepository;
import com.nightmarket.repository.ConversationRepository;
import com.nightmarket.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service managing marketplace conversations and persistent chat messages.
 */
@Service
public class MessageService {

    private static final Logger log = LoggerFactory.getLogger(MessageService.class);

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProductRepository productRepository;

    public MessageService(ConversationRepository conversationRepository,
                          ChatMessageRepository chatMessageRepository,
                          ProductRepository productRepository) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.productRepository = productRepository;
    }

    /**
     * Finds an existing conversation for the listing and buyer, or creates a new one.
     */
    public ConversationResponse startOrGetConversation(String listingId, String buyerId, String buyerName) {
        if (listingId == null || listingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Listing ID is required.");
        }

        String effectiveBuyerId = (buyerId != null && !buyerId.trim().isEmpty()) ? buyerId.trim() : "demo-buyer";
        String effectiveBuyerName = (buyerName != null && !buyerName.trim().isEmpty()) ? buyerName.trim() : "Student Buyer";

        return conversationRepository.findByListingIdAndBuyerId(listingId, effectiveBuyerId)
                .map(conv -> toConversationResponse(conv, effectiveBuyerId))
                .orElseGet(() -> {
                    Product product = productRepository.findById(listingId)
                            .orElseThrow(() -> new ListingNotFoundException(listingId));

                    String sellerId = product.getSellerId() != null ? product.getSellerId() : "demo-user";
                    String sellerName = product.getSellerName() != null ? product.getSellerName() : "Student Seller";
                    String primaryImage = (product.getImageUrls() != null && !product.getImageUrls().isEmpty())
                            ? product.getImageUrls().get(0) : "";

                    Conversation conversation = new Conversation(
                            product.getId(),
                            product.getTitle(),
                            primaryImage,
                            product.getPrice(),
                            sellerId,
                            sellerName,
                            effectiveBuyerId,
                            effectiveBuyerName
                    );

                    // Add an initial greeting from the seller so the buyer has context
                    conversation.setLastMessage("Hi! Thanks for your interest in " + product.getTitle() + ".");
                    Conversation saved = conversationRepository.save(conversation);
                    log.info("Created new conversation {} for listing {}", saved.getId(), listingId);

                    return toConversationResponse(saved, effectiveBuyerId);
                });
    }

    /**
     * Sends a new chat message within an existing conversation.
     */
    public MessageResponse sendMessage(String conversationId, String senderId, String senderName, SendMessageRequest request) {
        if (conversationId == null || conversationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Conversation ID is required.");
        }
        if (request == null || request.getContent() == null || request.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty.");
        }

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with ID: " + conversationId));

        String effectiveSenderId = (senderId != null && !senderId.trim().isEmpty()) ? senderId.trim() : "demo-buyer";
        String effectiveSenderName = (senderName != null && !senderName.trim().isEmpty()) ? senderName.trim() : "Student Buyer";

        // Determine recipient
        String receiverId = effectiveSenderId.equals(conversation.getBuyerId())
                ? conversation.getSellerId() : conversation.getBuyerId();

        ChatMessage message = new ChatMessage();
        message.setConversationId(conversation.getId());
        message.setListingId(conversation.getListingId());
        message.setSenderId(effectiveSenderId);
        message.setSenderName(effectiveSenderName);
        message.setReceiverId(receiverId);
        message.setContent(request.getContent().trim());
        message.setType(request.getType() != null ? request.getType() : MessageType.TEXT);
        message.setMetadataOfferAmount(request.getMetadataOfferAmount());
        message.setMetadataOfferId(request.getMetadataOfferId());
        message.setSentAt(LocalDateTime.now());
        message.setRead(false);

        ChatMessage saved = chatMessageRepository.save(message);

        // Update conversation last message timestamp
        conversation.setLastMessage(saved.getContent());
        conversationRepository.save(conversation);

        log.info("Persisted message {} in conversation {}", saved.getId(), conversationId);

        MessageResponse response = toMessageResponse(saved);
        response.setSentByCurrentUser(true);
        return response;
    }

    /**
     * Retrieves all messages in a conversation, ordered chronologically.
     */
    public List<MessageResponse> getMessages(String conversationId, String currentUserId) {
        if (conversationId == null || conversationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Conversation ID is required.");
        }

        if (!conversationRepository.existsById(conversationId)) {
            throw new ResourceNotFoundException("Conversation not found with ID: " + conversationId);
        }

        String effectiveUserId = (currentUserId != null && !currentUserId.trim().isEmpty()) ? currentUserId.trim() : "demo-buyer";

        return chatMessageRepository.findByConversationIdOrderBySentAtAsc(conversationId).stream()
                .map(m -> {
                    MessageResponse r = toMessageResponse(m);
                    r.setSentByCurrentUser(effectiveUserId.equals(m.getSenderId()));
                    return r;
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a conversation by ID.
     */
    public ConversationResponse getConversationById(String conversationId, String currentUserId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with ID: " + conversationId));
        return toConversationResponse(conversation, currentUserId);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private ConversationResponse toConversationResponse(Conversation c, String currentUserId) {
        ConversationResponse r = new ConversationResponse();
        r.setId(c.getId());
        r.setListingId(c.getListingId());
        r.setListingTitle(c.getListingTitle());
        r.setListingImageUrl(c.getListingImageUrl());
        r.setListingPrice(c.getListingPrice());
        r.setLastMessage(c.getLastMessage());
        r.setLastMessageTime(c.getLastMessageTime());

        boolean isBuyer = currentUserId != null && currentUserId.equals(c.getBuyerId());
        r.setOtherParticipantId(isBuyer ? c.getSellerId() : c.getBuyerId());
        r.setOtherParticipantName(isBuyer ? c.getSellerName() : c.getBuyerName());
        r.setUnreadCount(0);
        return r;
    }

    private MessageResponse toMessageResponse(ChatMessage m) {
        MessageResponse r = new MessageResponse();
        r.setId(m.getId());
        r.setConversationId(m.getConversationId());
        r.setSenderId(m.getSenderId());
        r.setSenderName(m.getSenderName());
        r.setContent(m.getContent());
        r.setType(m.getType());
        r.setMetadataOfferAmount(m.getMetadataOfferAmount());
        r.setMetadataOfferId(m.getMetadataOfferId());
        r.setRead(m.isRead());
        r.setSentAt(m.getSentAt());
        return r;
    }
}
