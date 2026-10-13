package com.nightmarket.domain.marketplace;

public enum ReportReason {
    SPAM("Spam or repetitive duplicate listing"),
    FRAUD_SCAM("Suspicious or fraudulent behavior"),
    INAPPROPRIATE("Inappropriate or prohibited campus item"),
    MISLEADING_PRICE("Misleading price or condition"),
    HARASSMENT("Harassment or abusive messaging"),
    OTHER("Other violation of campus guidelines");

    private final String description;

    ReportReason(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
