package com.ecommerce.reglog.entity;

import com.ecommerce.reglog.enums.UserStatus;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email"),
    @Index(name = "idx_users_phone", columnList = "phone")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(name = "email", unique = true, length = 150)
    private String email;

    @Column(name = "phone", unique = true, length = 20)
    private String phone;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(name = "auth_preference", nullable = false, length = 20)
    private String authPreference = "PASSWORD"; // "PASSWORD" or "OTP"

    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified = false;

    @Column(name = "is_phone_verified", nullable = false)
    private Boolean isPhoneVerified = false;

    @Column(name = "role", nullable = false, length = 20)
    private String role = "ROLE_CUSTOMER";

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @Transient
    private Instant lastLoginAt;

    public User() {
    }

    public User(String fullName, String email, String phone, String passwordHash, String authPreference) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.authPreference = authPreference != null ? authPreference : "PASSWORD";
        this.isEmailVerified = email != null && !email.isBlank();
        this.isPhoneVerified = phone != null && !phone.isBlank();
        this.role = "ROLE_CUSTOMER";
        this.status = UserStatus.ACTIVE;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = Instant.now();
        }
        if (this.role == null) {
            this.role = "ROLE_CUSTOMER";
        }
        if (this.status == null) {
            this.status = UserStatus.ACTIVE;
        }
        if (this.isEmailVerified == null) {
            this.isEmailVerified = false;
        }
        if (this.isPhoneVerified == null) {
            this.isPhoneVerified = false;
        }
        if (this.authPreference == null) {
            this.authPreference = "PASSWORD";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // Standard Getters & Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getAuthPreference() {
        return authPreference;
    }

    public void setAuthPreference(String authPreference) {
        this.authPreference = authPreference;
    }

    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }

    public Boolean getIsPhoneVerified() {
        return isPhoneVerified;
    }

    public void setIsPhoneVerified(Boolean isPhoneVerified) {
        this.isPhoneVerified = isPhoneVerified;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public UserStatus getStatus() {
        return status != null ? status : UserStatus.ACTIVE;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public void setStatus(String statusStr) {
        try {
            this.status = UserStatus.valueOf(statusStr);
        } catch (Exception e) {
            this.status = UserStatus.ACTIVE;
        }
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    // Convenience & Compatibility methods
    public String getFirstName() {
        if (fullName == null || fullName.isBlank()) return "";
        return fullName.contains(" ") ? fullName.split(" ", 2)[0] : fullName;
    }

    public void setFirstName(String firstName) {
        String lastName = getLastName();
        if (firstName != null && !firstName.isBlank()) {
            this.fullName = lastName.isBlank() ? firstName.trim() : (firstName.trim() + " " + lastName.trim());
        }
    }

    public String getLastName() {
        if (fullName == null || fullName.isBlank()) return "";
        return fullName.contains(" ") ? fullName.split(" ", 2)[1] : "";
    }

    public void setLastName(String lastName) {
        String firstName = getFirstName();
        if (lastName != null && !lastName.isBlank()) {
            this.fullName = firstName.isBlank() ? lastName.trim() : (firstName.trim() + " " + lastName.trim());
        }
    }

    public String getPhoneNumber() {
        return phone;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phone = phoneNumber;
    }



    public Set<Role> getRoles() {
        Set<Role> roles = new HashSet<>();
        try {
            com.ecommerce.reglog.enums.ERole eRole = com.ecommerce.reglog.enums.ERole.valueOf(this.role);
            roles.add(new Role(eRole, this.role));
        } catch (Exception e) {
            roles.add(new Role(com.ecommerce.reglog.enums.ERole.ROLE_CUSTOMER, "Customer"));
        }
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        if (roles != null && !roles.isEmpty()) {
            this.role = roles.iterator().next().getName().name();
        }
    }

    public List<Address> getAddresses() {
        return new ArrayList<>();
    }

    public void addAddress(Address address) {
        // no-op for now
    }

    public void removeAddress(Address address) {
        // no-op for now
    }
}
