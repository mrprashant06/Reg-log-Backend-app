package com.ecommerce.reglog.service;

import com.ecommerce.reglog.dto.request.RazorpayVerifyRequest;
import com.ecommerce.reglog.dto.response.OrderResponse;
import com.ecommerce.reglog.dto.response.RazorpayOrderResponse;

public interface PaymentService {
    RazorpayOrderResponse createRazorpayOrder(Long userId, Long orderId);
    OrderResponse verifyRazorpayPayment(Long userId, RazorpayVerifyRequest request);
    OrderResponse processCodPayment(Long userId, Long orderId);
}
