package com.nightmarket.repository;

import com.nightmarket.domain.marketplace.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Spring Data MongoDB repository for Chat Messages.
 */
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findByConversationIdOrderBySentAtAsc(String conversationId);

    long countByConversationId(String conversationId);
}
