package com.ecommerce.reglog.service.impl;

import com.ecommerce.reglog.dto.request.ForgotPasswordRequest;
import com.ecommerce.reglog.dto.request.LoginRequest;
import com.ecommerce.reglog.dto.request.RegisterRequest;
import com.ecommerce.reglog.dto.request.ResetPasswordRequest;
import com.ecommerce.reglog.dto.response.AddressResponse;
import com.ecommerce.reglog.dto.response.ApiResponse;
import com.ecommerce.reglog.dto.response.AuthResponse;
import com.ecommerce.reglog.dto.response.UserProfileResponse;
import com.ecommerce.reglog.entity.Address;
import com.ecommerce.reglog.entity.PasswordResetToken;
import com.ecommerce.reglog.entity.Role;
import com.ecommerce.reglog.entity.User;
import com.ecommerce.reglog.enums.AddressType;
import com.ecommerce.reglog.enums.ERole;
import com.ecommerce.reglog.enums.UserStatus;
import com.ecommerce.reglog.exception.BadRequestException;
import com.ecommerce.reglog.exception.ResourceNotFoundException;
import com.ecommerce.reglog.repository.PasswordResetTokenRepository;
import com.ecommerce.reglog.repository.RoleRepository;
import com.ecommerce.reglog.repository.UserRepository;
import com.ecommerce.reglog.security.JwtTokenProvider;
import com.ecommerce.reglog.security.UserPrincipal;
import com.ecommerce.reglog.service.AuthService;
import com.ecommerce.reglog.service.EmailService;
import com.ecommerce.reglog.service.TokenBlacklistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final TokenBlacklistService tokenBlacklistService;
    private final EmailService emailService;

    public AuthServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider,
                           TokenBlacklistService tokenBlacklistService,
                           EmailService emailService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.tokenBlacklistService = tokenBlacklistService;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new BadRequestException("Email address is already registered: " + request.getEmail());
        }

        // Retrieve or initialize default Customer role
        Role customerRole = roleRepository.findByName(ERole.ROLE_CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(ERole.ROLE_CUSTOMER, "Standard E-commerce Customer")));

        // Create new user entity
        User user = new User();
        user.setEmail(request.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setStatus(UserStatus.ACTIVE);
        user.setRoles(Set.of(customerRole));

        // Add initial shipping address if provided
        if (StringUtils.hasText(request.getStreetAddress()) && StringUtils.hasText(request.getCity())) {
            Address address = new Address();
            address.setAddressType(AddressType.SHIPPING);
            address.setStreetAddress(request.getStreetAddress());
            address.setApartmentSuite(request.getApartmentSuite());
            address.setCity(request.getCity());
            address.setState(request.getState());
            address.setPostalCode(request.getPostalCode());
            address.setCountry(request.getCountry());
            address.setDefault(true);
            user.addAddress(address);
        }

        User savedUser = userRepository.save(user);
        logger.info("New user registered successfully: id={}, email={}", savedUser.getId(), savedUser.getEmail());

        // Dispatch welcome email
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());

        return mapToUserProfileResponse(savedUser);
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String jwt = tokenProvider.generateToken(authentication);

        // Update last login timestamp
        User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        UserProfileResponse userProfile = mapToUserProfileResponse(user);
        return new AuthResponse(jwt, tokenProvider.getJwtExpirationMs(), userProfile);
    }

    @Override
    @Transactional
    public ApiResponse<Void> logout(String token) {
        if (!StringUtils.hasText(token)) {
            throw new BadRequestException("Token is required to perform logout");
        }

        try {
            Date expiryDate = tokenProvider.getExpirationDateFromToken(token);
            tokenBlacklistService.revokeToken(token, expiryDate.toInstant());
            SecurityContextHolder.clearContext();
            return ApiResponse.success("User logged out successfully. Session invalidated.");
        } catch (Exception ex) {
            logger.warn("Logout token processing notice: {}", ex.getMessage());
            SecurityContextHolder.clearContext();
            return ApiResponse.success("Logged out successfully");
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> forgotPassword(ForgotPasswordRequest request) {
        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(request.getEmail().trim());

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Invalidate/delete any previous unused reset tokens for this user
            passwordResetTokenRepository.deleteByUser(user);

            // Generate secure random reset token
            String resetToken = UUID.randomUUID().toString();
            Instant expiryDate = Instant.now().plusMillis(tokenProvider.getResetTokenExpirationMs());

            PasswordResetToken passwordResetToken = new PasswordResetToken(resetToken, user, expiryDate);
            passwordResetTokenRepository.save(passwordResetToken);

            // Send password reset email
            emailService.sendPasswordResetEmail(user.getEmail(), resetToken);
            logger.info("Password reset token generated and sent for user email: {}", user.getEmail());
        } else {
            logger.warn("Password reset requested for non-existent email: {}", request.getEmail());
        }

        // Generic response to protect against user enumeration
        return ApiResponse.success("If the email is registered in our system, you will receive password reset instructions.");
    }

    @Override
    @Transactional
    public ApiResponse<Void> resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirmation password do not match");
        }

        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(request.getToken().trim())
                .orElseThrow(() -> new BadRequestException("Invalid or expired password reset token"));

        if (resetToken.isUsed()) {
            throw new BadRequestException("This password reset token has already been used");
        }

        if (resetToken.isExpired()) {
            throw new BadRequestException("This password reset token has expired. Please request a new one");
        }

        User user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Invalidate token
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        logger.info("Password successfully reset for user: {}", user.getEmail());
        return ApiResponse.success("Password reset successfully. You may now log in with your new password.");
    }

    private UserProfileResponse mapToUserProfileResponse(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toSet());

        List<AddressResponse> addressResponses = user.getAddresses().stream()
                .map(addr -> new AddressResponse(
                        addr.getId(),
                        addr.getAddressType(),
                        addr.getStreetAddress(),
                        addr.getApartmentSuite(),
                        addr.getCity(),
                        addr.getState(),
                        addr.getPostalCode(),
                        addr.getCountry(),
                        addr.isDefault()
                ))
                .collect(Collectors.toList());

        return new UserProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhoneNumber(),
                user.getStatus(),
                roles,
                addressResponses,
                user.getLastLoginAt(),
                user.getCreatedAt()
        );
    }
}
