package com.ecommerce.reglog.dto.request;

import jakarta.validation.constraints.NotBlank;

public class OrderCancelRequest {

    @NotBlank(message = "Cancellation reason is required")
    private String reason;

    public OrderCancelRequest() {}

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
