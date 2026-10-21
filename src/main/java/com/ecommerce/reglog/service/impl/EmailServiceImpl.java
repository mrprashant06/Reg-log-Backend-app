package com.ecommerce.reglog.service.impl;

import com.ecommerce.reglog.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${app.client.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${spring.mail.username:noreply@ecommerce.com}")
    private String fromEmail;

    // Optional JavaMailSender: if mail properties are not provided, Spring won't fail startup
    public EmailServiceImpl(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
        String subject = "Password Reset Request - E-commerce Store";
        String messageBody = """
                Hello,
                
                You have requested to reset your password for your E-commerce account.
                Please click the link below to set a new password (valid for 15 minutes):
                
                %s
                
                If you did not request this password reset, please ignore this email or contact support immediately.
                
                Warm regards,
                E-commerce Security Team
                """.formatted(resetUrl);

        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(toEmail);
                message.setSubject(subject);
                message.setText(messageBody);
                mailSender.send(message);
                logger.info("Password reset email successfully dispatched to: {}", toEmail);
                return;
            } catch (Exception ex) {
                logger.warn("Could not dispatch email via SMTP (using fallback log): {}", ex.getMessage());
            }
        }

        // Mock / Local logging fallback for seamless development & offline testing
        logger.info("""
                ================================================================================
                [EMAIL NOTIFICATION SERVICE - PASSWORD RESET]
                To: %s
                Subject: %s
                Reset Token: %s
                Reset Link: %s
                ================================================================================
                """.formatted(toEmail, subject, resetToken, resetUrl));
    }

    @Override
    public void sendWelcomeEmail(String toEmail, String firstName) {
        String subject = "Welcome to our E-Commerce Store!";
        String messageBody = """
                Hello %s,
                
                Thank you for registering an account with our E-commerce store!
                We are thrilled to have you with us.
                
                You can now log in, browse our catalog, and track your orders.
                
                Happy Shopping!
                E-commerce Customer Experience Team
                """.formatted(firstName);

        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setFrom(fromEmail);
                message.setTo(toEmail);
                message.setSubject(subject);
                message.setText(messageBody);
                mailSender.send(message);
                logger.info("Welcome email sent to: {}", toEmail);
                return;
            } catch (Exception ex) {
                logger.warn("Could not dispatch welcome email via SMTP: {}", ex.getMessage());
            }
        }

        logger.info("[EMAIL NOTIFICATION SERVICE - WELCOME] Welcome email sent to {}: {}", toEmail, messageBody);
    }
}
