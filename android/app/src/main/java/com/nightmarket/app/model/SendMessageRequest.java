package com.nightmarket.app.model;

import com.google.gson.annotations.SerializedName;
import java.math.BigDecimal;

/**
 * Android request payload for sending a chat message.
 */
public class SendMessageRequest {

    @SerializedName("content")
    private String content;

    @SerializedName("type")
    private String type = "TEXT";

    @SerializedName("metadataOfferAmount")
    private BigDecimal metadataOfferAmount;

    @SerializedName("metadataOfferId")
    private String metadataOfferId;

    public SendMessageRequest() {}

    public SendMessageRequest(String content) {
        this.content = content;
        this.type = "TEXT";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
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
