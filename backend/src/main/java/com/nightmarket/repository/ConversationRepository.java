package com.nightmarket.repository;

import com.nightmarket.domain.marketplace.Conversation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data MongoDB repository for Conversations.
 */
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    Optional<Conversation> findByListingIdAndBuyerId(String listingId, String buyerId);

    List<Conversation> findByListingId(String listingId);

    List<Conversation> findBySellerIdOrBuyerIdOrderByLastMessageTimeDesc(String sellerId, String buyerId);
}
