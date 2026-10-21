package com.ecommerce.reglog.controller;

import com.ecommerce.reglog.dto.request.AddressRequest;
import com.ecommerce.reglog.dto.request.ChangePasswordRequest;
import com.ecommerce.reglog.dto.request.UpdateProfileRequest;
import com.ecommerce.reglog.dto.response.AddressResponse;
import com.ecommerce.reglog.dto.response.ApiResponse;
import com.ecommerce.reglog.dto.response.UserProfileResponse;
import com.ecommerce.reglog.security.UserPrincipal;
import com.ecommerce.reglog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Account Management", description = "Endpoints for managing customer profile, addresses, and account security")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile")
    @Operation(summary = "Get Profile Details", description = "Retrieves profile and address book of authenticated user.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserProfileResponse profile = userService.getCurrentUserProfile(userPrincipal.getId());
        return ResponseEntity.ok(ApiResponse.success("User profile retrieved", profile));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update Profile", description = "Updates first name, last name, and phone number.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody UpdateProfileRequest request) {
        UserProfileResponse updatedProfile = userService.updateProfile(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", updatedProfile));
    }

    @PostMapping("/change-password")
    @Operation(summary = "Change Password", description = "Updates password when providing valid current password.")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(userPrincipal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Password changed successfully"));
    }

    @PostMapping("/addresses")
    @Operation(summary = "Add Address", description = "Adds a shipping or billing address to customer's account.")
    public ResponseEntity<ApiResponse<AddressResponse>> addAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @Valid @RequestBody AddressRequest request) {
        AddressResponse addressResponse = userService.addAddress(userPrincipal.getId(), request);
        return new ResponseEntity<>(
                ApiResponse.success("Address added successfully", addressResponse),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/addresses/{addressId}")
    @Operation(summary = "Delete Address", description = "Removes an address from the user's account.")
    public ResponseEntity<ApiResponse<Void>> deleteAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long addressId) {
        userService.deleteAddress(userPrincipal.getId(), addressId);
        return ResponseEntity.ok(ApiResponse.success("Address deleted successfully"));
    }

    @PatchMapping("/addresses/{addressId}/default")
    @Operation(summary = "Set Default Address", description = "Sets an address as the primary/default shipping address.")
    public ResponseEntity<ApiResponse<Void>> setDefaultAddress(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable Long addressId) {
        userService.setDefaultAddress(userPrincipal.getId(), addressId);
        return ResponseEntity.ok(ApiResponse.success("Default address updated successfully"));
    }
}
