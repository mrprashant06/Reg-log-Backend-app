package com.ecommerce.reglog.service;

public interface EmailService {

    void sendPasswordResetEmail(String toEmail, String resetToken);

    void sendWelcomeEmail(String toEmail, String firstName);
}
