package com.ecommerce.reglog.dto.request;

import jakarta.validation.constraints.NotBlank;

public class SendOtpDto {

    @NotBlank(message = "Identifier (Email or Phone) is required")
    private String identifier;

    private String purpose = "REGISTRATION"; // "REGISTRATION" or "LOGIN"

    public SendOtpDto() {}

    public SendOtpDto(String identifier, String purpose) {
        this.identifier = identifier;
        this.purpose = purpose;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
