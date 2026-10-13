package com.nightmarket.dto.request;

import com.nightmarket.domain.marketplace.MessageType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class SendMessageRequest {

    @NotBlank(message = "Message content is required")
    @Size(max = 2000, message = "Message cannot exceed 2000 characters")
    private String content;

    private MessageType type = MessageType.TEXT;
    private BigDecimal metadataOfferAmount;
    private String metadataOfferId;

    public SendMessageRequest() {}

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public BigDecimal getMetadataOfferAmount() {
        return metadataOfferAmount;
    }

    public void setMetadataOfferAmount(BigDecimal metadataOfferAmount) {
        this.metadataOfferAmount = metadataOfferAmount;
    }

    public String getMetadataOfferId() {
        return metadataOfferId;
    }

    public void setMetadataOfferId(String metadataOfferId) {
        this.metadataOfferId = metadataOfferId;
    }
}
