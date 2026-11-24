package com.ecommerce.reglog.dto.request;

import jakarta.validation.constraints.NotBlank;

public class OrderReturnRequest {

    @NotBlank(message = "Return reason is required")
    private String reason;

    public OrderReturnRequest() {}

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
