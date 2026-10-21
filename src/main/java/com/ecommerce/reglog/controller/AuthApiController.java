package com.ecommerce.reglog.controller;

import com.ecommerce.reglog.dto.request.*;
import com.ecommerce.reglog.entity.OtpVerification;
import com.ecommerce.reglog.entity.User;
import com.ecommerce.reglog.repository.OtpVerificationRepository;
import com.ecommerce.reglog.repository.UserRepository;
import com.ecommerce.reglog.security.JwtTokenProvider;
import com.ecommerce.reglog.security.UserPrincipal;
import com.ecommerce.reglog.service.TokenBlacklistService;
import com.ecommerce.reglog.util.ValidationUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@Tag(name = "E-Commerce Glassmorphic Auth API", description = "Endpoints for Registration, Login, Forgot Password, Change Password, and OTP Verification")
public class AuthApiController {

    private static final Logger logger = LoggerFactory.getLogger(AuthApiController.class);
    private static final SecureRandom secureRandom = new SecureRandom();

    private final UserRepository userRepository;
    private final OtpVerificationRepository otpVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthApiController(UserRepository userRepository,
                             OtpVerificationRepository otpVerificationRepository,
                             PasswordEncoder passwordEncoder,
                             JwtTokenProvider jwtTokenProvider,
                             TokenBlacklistService tokenBlacklistService) {
        this.userRepository = userRepository;
        this.otpVerificationRepository = otpVerificationRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    /**
     * Dispatch a 6-digit OTP code to the provided email or phone identifier.
     */
    @PostMapping("/send-otp")
    @Operation(summary = "Generate and Send 6-digit OTP", description = "Generates a cryptographically secure 6-digit OTP for Registration or Login verification.")
    public ResponseEntity<Map<String, Object>> sendOtp(@Valid @RequestBody SendOtpDto dto) {
        String identifier = dto.getIdentifier().trim();
        String purpose = dto.getPurpose() != null ? dto.getPurpose().toUpperCase().trim() : "REGISTRATION";

        // Generate 6-digit OTP
        int code = 100000 + secureRandom.nextInt(900000);
        String otpCode = String.valueOf(code);

        // Store OTP with 5 minutes validity
        Instant expiresAt = Instant.now().plusSeconds(300);
        OtpVerification otpVerification = new OtpVerification(identifier, otpCode, purpose, expiresAt);
        otpVerificationRepository.save(otpVerification);

        logger.info("=================================================");
        logger.info(">>> GENERATED OTP: [{}] FOR: [{}] PURPOSE: [{}]", otpCode, identifier, purpose);
        logger.info("=================================================");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "6-digit OTP sent successfully to " + identifier);
        response.put("identifier", identifier);
        response.put("purpose", purpose);
        response.put("expiresInSeconds", 300);
        response.put("otp", otpCode);

        return ResponseEntity.ok(response);
    }

    /**
     * Verify an OTP code before finalizing action.
     */
    @PostMapping("/verify-otp")
    @Operation(summary = "Verify OTP Code", description = "Verifies the 6-digit OTP code against the database record.")
    public ResponseEntity<Map<String, Object>> verifyOtp(@Valid @RequestBody VerifyOtpDto dto) {
        String identifier = dto.getIdentifier().trim();
        String purpose = dto.getPurpose() != null ? dto.getPurpose().toUpperCase().trim() : "REGISTRATION";
        String otpCode = dto.getOtpCode().trim();

        Optional<OtpVerification> optOtp = otpVerificationRepository
                .findTopByIdentifierAndPurposeAndIsConsumedFalseOrderByCreatedAtDesc(identifier, purpose);

        if (optOtp.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "No active OTP request found for " + identifier + ". Please request a new OTP.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        OtpVerification record = optOtp.get();
        if (record.getExpiresAt().isBefore(Instant.now())) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "The OTP code has expired. Please request a new OTP.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        if (record.getAttemptsCount() >= 5) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Maximum verification attempts exceeded. Please generate a new OTP.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        if (!record.getOtpCode().equals(otpCode)) {
            record.setAttemptsCount(record.getAttemptsCount() + 1);
            otpVerificationRepository.save(record);

            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Invalid 6-digit OTP code. Please check and try again.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        // Mark OTP consumed
        record.setIsConsumed(true);
        otpVerificationRepository.save(record);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "OTP verified successfully!");
        return ResponseEntity.ok(response);
    }

    /**
     * User Sign Up endpoint with strict validation rules:
     * - Full name: min 3, max 100 characters
     * - Email / Mobile number: unique, valid format
     * - Password: min 8 characters, 1 uppercase, 1 lowercase, 1 numeric, 1 special character
     * - Confirmed password must match
     * - Password encrypted via BCrypt
     */
    @PostMapping("/register")
    @Operation(summary = "Register User Account", description = "Registers user into MySQL database using Password or OTP with comprehensive validation.")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody RegisterUserDto dto) {
        // 1. Validate Full Name
        if (!ValidationUtils.isValidFullName(dto.getFullName())) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Full Name must be between 3 and 100 characters long.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        String identifier = dto.getIdentifier().trim();
        String authPreference = dto.getAuthPreference() != null ? dto.getAuthPreference().toUpperCase().trim() : "PASSWORD";
        boolean isEmail = identifier.contains("@");

        String email = null;
        String phone = null;

        // 2. Validate Contact Format & Uniqueness
        if (isEmail) {
            email = identifier.toLowerCase();
            if (!ValidationUtils.isValidEmail(email)) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "Please enter a valid email address (e.g. name@domain.com).");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            }
            if (userRepository.existsByEmailIgnoreCase(email)) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "An account with email " + email + " already exists. Please log in.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
            }
        } else {
            phone = ValidationUtils.cleanPhone(identifier);
            if (!ValidationUtils.isValidPhone(phone)) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "Mobile number must contain between 10 and 15 digits.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            }
            if (userRepository.existsByPhone(phone)) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "An account with mobile number " + phone + " already exists. Please log in.");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
            }
        }

        String passwordHash = null;

        // 3. Validate Password Security Requirements
        if ("PASSWORD".equals(authPreference)) {
            if (!StringUtils.hasText(dto.getPassword())) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "Password is required for Password verification mode.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            }

            String passwordError = ValidationUtils.getPasswordValidationError(dto.getPassword());
            if (passwordError != null) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", passwordError);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            }

            if (dto.getConfirmPassword() == null || !dto.getPassword().equals(dto.getConfirmPassword())) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "Confirmed password does not match the password.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            }

            passwordHash = passwordEncoder.encode(dto.getPassword());
        } else if ("OTP".equals(authPreference)) {
            if (!StringUtils.hasText(dto.getOtpCode())) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "Please enter the 6-digit OTP code to complete sign up.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            }

            Optional<OtpVerification> optOtp = otpVerificationRepository
                    .findTopByIdentifierAndPurposeAndIsConsumedFalseOrderByCreatedAtDesc(identifier, "REGISTRATION");

            if (optOtp.isPresent()) {
                OtpVerification record = optOtp.get();
                if (!record.getOtpCode().equals(dto.getOtpCode().trim())) {
                    Map<String, Object> err = new HashMap<>();
                    err.put("success", false);
                    err.put("message", "Invalid OTP code provided.");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
                }
                record.setIsConsumed(true);
                otpVerificationRepository.save(record);
            }
            passwordHash = passwordEncoder.encode("OTP_AUTH_" + secureRandom.nextInt(1000000));
        }

        // 4. Save User in Database
        User user = new User();
        user.setFullName(dto.getFullName().trim());
        user.setEmail(email);
        user.setPhone(phone);
        user.setPasswordHash(passwordHash);
        user.setAuthPreference(authPreference);
        user.setIsEmailVerified(isEmail);
        user.setIsPhoneVerified(!isEmail);
        user.setRole("ROLE_CUSTOMER");
        user.setStatus("ACTIVE");

        User savedUser = userRepository.save(user);
        logger.info("New User registered successfully in MySQL: ID={}, Name={}, Contact={}",
                savedUser.getId(), savedUser.getFullName(), identifier);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", savedUser.getId());
        userMap.put("fullName", savedUser.getFullName());
        userMap.put("email", savedUser.getEmail());
        userMap.put("phone", savedUser.getPhone());
        userMap.put("authPreference", savedUser.getAuthPreference());
        userMap.put("role", savedUser.getRole());
        userMap.put("status", savedUser.getStatus());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Account created successfully! Redirecting to login...");
        response.put("user", userMap);
        response.put("registeredIdentifier", identifier);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * User Login endpoint.
     */
    @PostMapping("/login")
    @Operation(summary = "User Login", description = "Authenticates user using Password or OTP and returns session JWT.")
    public ResponseEntity<Map<String, Object>> login(@Valid @RequestBody LoginUserDto dto) {
        String identifier = dto.getIdentifier().trim();
        String authMode = dto.getAuthMode() != null ? dto.getAuthMode().toUpperCase().trim() : "PASSWORD";

        Optional<User> optUser = userRepository.findByIdentifier(identifier);
        if (optUser.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "No account registered with identifier: " + identifier);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        User user = optUser.get();

        if ("PASSWORD".equals(authMode)) {
            if (!StringUtils.hasText(dto.getPassword())) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "Password is required for login.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            }
            if (user.getPasswordHash() == null || !passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "Invalid credentials. Please verify your password.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
            }
        } else if ("OTP".equals(authMode)) {
            if (!StringUtils.hasText(dto.getOtpCode())) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "Please enter the 6-digit OTP code to log in.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            }

            Optional<OtpVerification> optOtp = otpVerificationRepository
                    .findTopByIdentifierAndPurposeAndIsConsumedFalseOrderByCreatedAtDesc(identifier, "LOGIN");

            if (optOtp.isEmpty()) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "No active OTP found. Please click 'Send OTP' first.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            }

            OtpVerification record = optOtp.get();
            if (record.getExpiresAt().isBefore(Instant.now())) {
                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "The OTP code has expired. Please request a new OTP.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            }

            if (!record.getOtpCode().equals(dto.getOtpCode().trim())) {
                record.setAttemptsCount(record.getAttemptsCount() + 1);
                otpVerificationRepository.save(record);

                Map<String, Object> err = new HashMap<>();
                err.put("success", false);
                err.put("message", "Invalid OTP code entered.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            }

            record.setIsConsumed(true);
            otpVerificationRepository.save(record);
        }

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        // Generate JWT Access Token
        UserPrincipal principal = UserPrincipal.create(user);
        String token = jwtTokenProvider.generateTokenFromUserPrincipal(principal);

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("fullName", user.getFullName());
        userMap.put("email", user.getEmail());
        userMap.put("phone", user.getPhone());
        userMap.put("authPreference", user.getAuthPreference());
        userMap.put("role", user.getRole());
        userMap.put("status", user.getStatus());
        userMap.put("lastLoginAt", user.getLastLoginAt());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Welcome back, " + user.getFullName() + "!");
        response.put("token", token);
        response.put("user", userMap);

        return ResponseEntity.ok(response);
    }

    /**
     * Forgot Password Step 1: Send Password Reset OTP
     * Accepts registered email or mobile number, validates user existence, generates 6-digit OTP.
     */
    @PostMapping("/forgot-password")
    @Operation(summary = "Forgot Password - Send OTP", description = "Sends 6-digit OTP to registered email or mobile number for password reset.")
    public ResponseEntity<Map<String, Object>> forgotPassword(@Valid @RequestBody ForgotPasswordDto dto) {
        String identifier = dto.getIdentifier().trim();

        // Check if user exists in database
        Optional<User> optUser = userRepository.findByIdentifier(identifier);
        if (optUser.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "No registered account found with that email or mobile number.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        int code = 100000 + secureRandom.nextInt(900000);
        String otpCode = String.valueOf(code);

        Instant expiresAt = Instant.now().plusSeconds(300);
        OtpVerification otpVerification = new OtpVerification(identifier, otpCode, "PASSWORD_RESET", expiresAt);
        otpVerificationRepository.save(otpVerification);

        logger.info("=================================================");
        logger.info(">>> PASSWORD RESET OTP [{}] GENERATED FOR [{}]", otpCode, identifier);
        logger.info("=================================================");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Password reset OTP sent to " + identifier);
        response.put("identifier", identifier);
        response.put("otp", otpCode);
        response.put("expiresInSeconds", 300);

        return ResponseEntity.ok(response);
    }

    /**
     * Forgot Password Step 2: Reset / Restart Password with verified OTP
     * Validates OTP, verifies new password complexity, confirms match, hashes with BCrypt, updates MySQL database.
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Forgot Password - Reset Password", description = "Resets user password after verifying OTP with strict password validation.")
    public ResponseEntity<Map<String, Object>> resetPasswordWithOtp(@Valid @RequestBody ResetPasswordWithOtpDto dto) {
        String identifier = dto.getIdentifier().trim();

        Optional<User> optUser = userRepository.findByIdentifier(identifier);
        if (optUser.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "No account found matching: " + identifier);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        User user = optUser.get();

        // 1. Verify OTP code
        Optional<OtpVerification> optOtp = otpVerificationRepository
                .findTopByIdentifierAndPurposeAndIsConsumedFalseOrderByCreatedAtDesc(identifier, "PASSWORD_RESET");

        if (optOtp.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "No active password reset OTP request found. Please request a new code.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        OtpVerification record = optOtp.get();
        if (record.getExpiresAt().isBefore(Instant.now())) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "The password reset OTP has expired. Please request a new code.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        if (!record.getOtpCode().equals(dto.getOtpCode().trim())) {
            record.setAttemptsCount(record.getAttemptsCount() + 1);
            otpVerificationRepository.save(record);

            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Invalid 6-digit OTP code entered.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        // 2. Validate New Password Requirements
        String passwordError = ValidationUtils.getPasswordValidationError(dto.getNewPassword());
        if (passwordError != null) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", passwordError);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        if (dto.getConfirmPassword() == null || !dto.getNewPassword().equals(dto.getConfirmPassword())) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Confirmed password does not match the new password.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        // 3. Ensure new password is not identical to current password
        if (user.getPasswordHash() != null && passwordEncoder.matches(dto.getNewPassword(), user.getPasswordHash())) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "New password cannot be the same as your current password.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        // 4. Update Password in MySQL
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        // Mark OTP consumed
        record.setIsConsumed(true);
        otpVerificationRepository.save(record);

        logger.info("Password successfully reset for user: {}", identifier);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Password has been reset successfully! Please log in with your new password.");
        return ResponseEntity.ok(response);
    }

    /**
     * Change Password for Authenticated User
     * Verifies current password, validates new password complexity, confirms match, hashes with BCrypt, updates MySQL database.
     */
    @PostMapping("/change-password")
    @Operation(summary = "Change Password (Authenticated)", description = "Updates password when providing valid current password.")
    public ResponseEntity<Map<String, Object>> changePassword(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ChangePasswordDto dto) {

        if (principal == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Unauthorized. Please log in first.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        Optional<User> optUser = userRepository.findById(principal.getId());
        if (optUser.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "User account not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        User user = optUser.get();

        // 1. Verify current password
        if (user.getPasswordHash() != null && !passwordEncoder.matches(dto.getCurrentPassword(), user.getPasswordHash())) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Current password entered is incorrect.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        // 2. Validate new password complexity
        String passwordError = ValidationUtils.getPasswordValidationError(dto.getNewPassword());
        if (passwordError != null) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", passwordError);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        if (dto.getConfirmPassword() == null || !dto.getNewPassword().equals(dto.getConfirmPassword())) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Confirmed password does not match the new password.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        // 3. Ensure new password != old password
        if (user.getPasswordHash() != null && passwordEncoder.matches(dto.getNewPassword(), user.getPasswordHash())) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "New password cannot be the same as your old password.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
        }

        // 4. Update Password in MySQL
        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        logger.info("Password successfully changed for user ID: {}", user.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Password changed successfully!");
        return ResponseEntity.ok(response);
    }

    /**
     * Get details of currently authenticated user.
     */
    @GetMapping("/me")
    @Operation(summary = "Get Profile", description = "Returns profile details for the authenticated user.")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "Unauthorized. Please log in.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
        }

        Optional<User> optUser = userRepository.findById(principal.getId());
        if (optUser.isEmpty()) {
            Map<String, Object> err = new HashMap<>();
            err.put("success", false);
            err.put("message", "User account not found.");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(err);
        }

        User user = optUser.get();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("id", user.getId());
        userMap.put("fullName", user.getFullName());
        userMap.put("email", user.getEmail());
        userMap.put("phone", user.getPhone());
        userMap.put("authPreference", user.getAuthPreference());
        userMap.put("role", user.getRole());
        userMap.put("status", user.getStatus());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("user", userMap);
        return ResponseEntity.ok(response);
    }

    /**
     * Sign Out and blacklist JWT token so it cannot be reused.
     */
    @PostMapping("/logout")
    @Operation(summary = "Sign Out", description = "Terminates the active session and invalidates the JWT access token.")
    public ResponseEntity<Map<String, Object>> logout(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            try {
                Date expirationDate = jwtTokenProvider.getExpirationDateFromToken(token);
                tokenBlacklistService.revokeToken(token, expirationDate.toInstant());
            } catch (Exception e) {
                logger.warn("Could not parse token expiration for blacklist revocation: {}", e.getMessage());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Signed out successfully. Token invalidated.");
        return ResponseEntity.ok(response);
    }
}
