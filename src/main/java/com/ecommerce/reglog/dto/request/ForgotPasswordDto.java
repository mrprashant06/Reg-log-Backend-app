package com.ecommerce.reglog.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordDto {

    @NotBlank(message = "Registered Email Address or Mobile Number is required")
    private String identifier;

    public ForgotPasswordDto() {}

    public ForgotPasswordDto(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
