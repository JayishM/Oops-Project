package com.nightmarket.dto.request;

import com.nightmarket.domain.marketplace.ReportReason;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateReportRequest {

    private String targetListingId;
    private String targetUserId;

    @NotNull(message = "Report reason is required")
    private ReportReason reason;

    @Size(max = 1000, message = "Report details cannot exceed 1000 characters")
    private String details;

    public CreateReportRequest() {}

    public String getTargetListingId() {
        return targetListingId;
    }

    public void setTargetListingId(String targetListingId) {
        this.targetListingId = targetListingId;
    }

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public ReportReason getReason() {
        return reason;
    }

    public void setReason(ReportReason reason) {
        this.reason = reason;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
