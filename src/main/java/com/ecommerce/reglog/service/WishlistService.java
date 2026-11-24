package com.ecommerce.reglog.service;

import com.ecommerce.reglog.dto.response.WishlistResponse;

public interface WishlistService {
    WishlistResponse getWishlist(Long userId);
    WishlistResponse addToWishlist(Long userId, Long productId);
    WishlistResponse removeFromWishlist(Long userId, Long productId);
    void clearWishlist(Long userId);
    boolean isProductInWishlist(Long userId, Long productId);
    WishlistResponse moveToCart(Long userId, Long productId, String size, String color);
}
