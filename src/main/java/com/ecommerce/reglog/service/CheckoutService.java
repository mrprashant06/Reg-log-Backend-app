package com.ecommerce.reglog.service;

import com.ecommerce.reglog.dto.request.PlaceOrderRequest;
import com.ecommerce.reglog.dto.response.CartResponse;
import com.ecommerce.reglog.dto.response.OrderResponse;

public interface CheckoutService {
    CartResponse validateCheckout(Long userId, Long addressId);
    OrderResponse placeOrder(Long userId, PlaceOrderRequest request);
}
