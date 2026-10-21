package com.ecommerce.reglog.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LoginUserDto {

    @NotBlank(message = "Identifier (Email or Phone) is required")
    private String identifier;

    private String authMode = "PASSWORD"; // "PASSWORD" or "OTP"

    private String password;

    private String otpCode;

    public LoginUserDto() {}

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getAuthMode() {
        return authMode;
    }

    public void setAuthMode(String authMode) {
        this.authMode = authMode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOtpCode() {
        return otpCode;
    }

    public void setOtpCode(String otpCode) {
        this.otpCode = otpCode;
    }
}
