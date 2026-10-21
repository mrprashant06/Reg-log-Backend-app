package com.ecommerce.reglog.service;

import com.ecommerce.reglog.dto.request.AddressRequest;
import com.ecommerce.reglog.dto.request.ChangePasswordRequest;
import com.ecommerce.reglog.dto.request.UpdateProfileRequest;
import com.ecommerce.reglog.dto.response.AddressResponse;
import com.ecommerce.reglog.dto.response.PageResponse;
import com.ecommerce.reglog.dto.response.UserProfileResponse;
import com.ecommerce.reglog.enums.ERole;
import com.ecommerce.reglog.enums.UserStatus;

public interface UserService {

    UserProfileResponse getCurrentUserProfile(Long userId);

    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);

    AddressResponse addAddress(Long userId, AddressRequest request);

    void deleteAddress(Long userId, Long addressId);

    void setDefaultAddress(Long userId, Long addressId);

    // Admin database management methods
    PageResponse<UserProfileResponse> adminGetAllUsers(int page, int size, String keyword);

    UserProfileResponse adminGetUserById(Long id);

    UserProfileResponse adminUpdateUserStatus(Long id, UserStatus newStatus);

    UserProfileResponse adminAssignRole(Long id, ERole roleName);
}
