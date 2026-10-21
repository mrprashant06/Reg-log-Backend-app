package com.ecommerce.reglog.controller;

import com.ecommerce.reglog.dto.request.UpdateUserStatusRequest;
import com.ecommerce.reglog.dto.response.ApiResponse;
import com.ecommerce.reglog.dto.response.PageResponse;
import com.ecommerce.reglog.dto.response.UserProfileResponse;
import com.ecommerce.reglog.enums.ERole;
import com.ecommerce.reglog.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Admin Database Management", description = "Administration endpoints to manage users, statuses, and role privileges")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Search and Paginate Users", description = "Admin database query with pagination and keyword filtering on name or email.")
    public ResponseEntity<ApiResponse<PageResponse<UserProfileResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        PageResponse<UserProfileResponse> users = userService.adminGetAllUsers(page, size, keyword);
        return ResponseEntity.ok(ApiResponse.success("Users fetched successfully", users));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get User Details by ID", description = "Admin query for single user details.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(@PathVariable Long id) {
        UserProfileResponse user = userService.adminGetUserById(id);
        return ResponseEntity.ok(ApiResponse.success("User fetched successfully", user));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update User Status", description = "Admin action to activate, suspend, or deactivate a customer account.")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserStatusRequest request) {
        UserProfileResponse updatedUser = userService.adminUpdateUserStatus(id, request.getStatus());
        return ResponseEntity.ok(ApiResponse.success("User status updated successfully", updatedUser));
    }

    @PostMapping("/{id}/roles")
    @Operation(summary = "Assign Role to User", description = "Admin action to grant additional privileges (e.g., ROLE_ADMIN, ROLE_MODERATOR).")
    public ResponseEntity<ApiResponse<UserProfileResponse>> assignRole(
            @PathVariable Long id,
            @RequestParam ERole role) {
        UserProfileResponse updatedUser = userService.adminAssignRole(id, role);
        return ResponseEntity.ok(ApiResponse.success("Role assigned successfully", updatedUser));
    }
}
