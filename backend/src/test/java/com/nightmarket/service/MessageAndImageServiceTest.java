package com.nightmarket.service;

import com.nightmarket.domain.marketplace.ChatMessage;
import com.nightmarket.domain.marketplace.Conversation;
import com.nightmarket.domain.product.Electronics;
import com.nightmarket.dto.request.SendMessageRequest;
import com.nightmarket.dto.response.ConversationResponse;
import com.nightmarket.dto.response.MessageResponse;
import com.nightmarket.exception.ListingNotFoundException;
import com.nightmarket.exception.ResourceNotFoundException;
import com.nightmarket.repository.ChatMessageRepository;
import com.nightmarket.repository.ConversationRepository;
import com.nightmarket.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("MessageService and ImageStorageService Unit Tests")
class MessageAndImageServiceTest {

    private ConversationRepository conversationRepository;
    private ChatMessageRepository chatMessageRepository;
    private ProductRepository productRepository;
    private MessageService messageService;

    private GridFsTemplate gridFsTemplate;
    private ImageStorageService imageStorageService;

    @BeforeEach
    void setUp() {
        conversationRepository = mock(ConversationRepository.class);
        chatMessageRepository = mock(ChatMessageRepository.class);
        productRepository = mock(ProductRepository.class);
        messageService = new MessageService(conversationRepository, chatMessageRepository, productRepository);

        gridFsTemplate = mock(GridFsTemplate.class);
        imageStorageService = new ImageStorageService(gridFsTemplate);
    }

    @Test
    @DisplayName("startOrGetConversation creates and returns new conversation when none exists")
    void testStartNewConversation() {
        Electronics p = new Electronics();
        p.setId("listing-100");
        p.setTitle("Casio Calculator");
        p.setPrice(new BigDecimal("750"));
        p.setSellerId("seller-1");
        p.setSellerName("Priya");
        p.setImageUrls(List.of("/api/images/calc.jpg"));

        when(conversationRepository.findByListingIdAndBuyerId("listing-100", "buyer-1"))
                .thenReturn(Optional.empty());
        when(productRepository.findById("listing-100")).thenReturn(Optional.of(p));
        when(conversationRepository.save(any(Conversation.class))).thenAnswer(inv -> {
            Conversation c = inv.getArgument(0);
            c.setId("conv-999");
            return c;
        });

        ConversationResponse res = messageService.startOrGetConversation("listing-100", "buyer-1", "Aarav");

        assertNotNull(res);
        assertEquals("conv-999", res.getId());
        assertEquals("listing-100", res.getListingId());
        assertEquals("Casio Calculator", res.getListingTitle());
        assertEquals("Priya", res.getOtherParticipantName());
        verify(conversationRepository).save(any(Conversation.class));
    }

    @Test
    @DisplayName("startOrGetConversation returns existing conversation without creating a duplicate")
    void testGetExistingConversation() {
        Conversation existing = new Conversation("listing-100", "Casio Calculator", "/img.jpg",
                new BigDecimal("750"), "seller-1", "Priya", "buyer-1", "Aarav");
        existing.setId("conv-existing");

        when(conversationRepository.findByListingIdAndBuyerId("listing-100", "buyer-1"))
                .thenReturn(Optional.of(existing));

        ConversationResponse res = messageService.startOrGetConversation("listing-100", "buyer-1", "Aarav");

        assertNotNull(res);
        assertEquals("conv-existing", res.getId());
        verify(productRepository, never()).findById(any());
        verify(conversationRepository, never()).save(any());
    }

    @Test
    @DisplayName("startOrGetConversation throws ListingNotFoundException for missing listing")
    void testStartConversationMissingListing() {
        when(conversationRepository.findByListingIdAndBuyerId("invalid-id", "buyer-1"))
                .thenReturn(Optional.empty());
        when(productRepository.findById("invalid-id")).thenReturn(Optional.empty());

        assertThrows(ListingNotFoundException.class, () ->
                messageService.startOrGetConversation("invalid-id", "buyer-1", "Aarav"));
    }

    @Test
    @DisplayName("sendMessage persists message and updates conversation lastMessage timestamp")
    void testSendMessageSuccess() {
        Conversation conv = new Conversation("listing-1", "Book", "", BigDecimal.TEN,
                "seller-1", "Priya", "buyer-1", "Aarav");
        conv.setId("conv-1");

        when(conversationRepository.findById("conv-1")).thenReturn(Optional.of(conv));
        when(chatMessageRepository.save(any(ChatMessage.class))).thenAnswer(inv -> {
            ChatMessage m = inv.getArgument(0);
            m.setId("msg-123");
            return m;
        });

        SendMessageRequest req = new SendMessageRequest();
        req.setContent("Hi! Can we meet at the library?");

        MessageResponse res = messageService.sendMessage("conv-1", "buyer-1", "Aarav", req);

        assertNotNull(res);
        assertEquals("msg-123", res.getId());
        assertEquals("Hi! Can we meet at the library?", res.getContent());
        assertTrue(res.isSentByCurrentUser());

        ArgumentCaptor<Conversation> convCaptor = ArgumentCaptor.forClass(Conversation.class);
        verify(conversationRepository).save(convCaptor.capture());
        assertEquals("Hi! Can we meet at the library?", convCaptor.getValue().getLastMessage());
    }

    @Test
    @DisplayName("sendMessage rejects empty or whitespace-only messages")
    void testSendMessageRejectsEmpty() {
        SendMessageRequest req = new SendMessageRequest();
        req.setContent("   ");

        assertThrows(IllegalArgumentException.class, () ->
                messageService.sendMessage("conv-1", "buyer-1", "Aarav", req));
    }

    @Test
    @DisplayName("ImageStorageService rejects empty file and non-image MIME types")
    void testImageValidation() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", new byte[0]);
        assertThrows(IllegalArgumentException.class, () -> imageStorageService.storeImage(emptyFile));

        MockMultipartFile textFile = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());
        assertThrows(IllegalArgumentException.class, () -> imageStorageService.storeImage(textFile));
    }
}
