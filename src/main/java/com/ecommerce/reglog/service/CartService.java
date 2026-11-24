package com.ecommerce.reglog.service;

import com.ecommerce.reglog.dto.request.AddToCartRequest;
import com.ecommerce.reglog.dto.request.UpdateCartItemRequest;
import com.ecommerce.reglog.dto.response.CartResponse;

public interface CartService {
    CartResponse getCartForUser(Long userId);
    CartResponse addToCart(Long userId, AddToCartRequest request);
    CartResponse updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request);
    CartResponse removeCartItem(Long userId, Long itemId);
    void clearCart(Long userId);
}
