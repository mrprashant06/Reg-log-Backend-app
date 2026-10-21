package com.ecommerce.reglog.service;

import com.ecommerce.reglog.dto.request.ForgotPasswordRequest;
import com.ecommerce.reglog.dto.request.LoginRequest;
import com.ecommerce.reglog.dto.request.RegisterRequest;
import com.ecommerce.reglog.dto.request.ResetPasswordRequest;
import com.ecommerce.reglog.dto.response.ApiResponse;
import com.ecommerce.reglog.dto.response.AuthResponse;
import com.ecommerce.reglog.dto.response.UserProfileResponse;

public interface AuthService {

    UserProfileResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    ApiResponse<Void> logout(String token);

    ApiResponse<String> forgotPassword(ForgotPasswordRequest request);

    ApiResponse<Void> resetPassword(ResetPasswordRequest request);
}
