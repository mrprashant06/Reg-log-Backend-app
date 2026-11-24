package com.ecommerce.reglog.controller;

import com.ecommerce.reglog.dto.response.ApiResponse;
import com.ecommerce.reglog.dto.response.WishlistResponse;
import com.ecommerce.reglog.security.UserPrincipal;
import com.ecommerce.reglog.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping({"/api/wishlist", "/api/v1/wishlist"})
@Tag(name = "Customer Wishlist", description = "Endpoints for managing customer saved items and wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @GetMapping
    @Operation(summary = "Get Customer Wishlist", description = "Returns all products saved in the user's wishlist.")
    public ResponseEntity<ApiResponse<WishlistResponse>> getWishlist(@AuthenticationPrincipal UserPrincipal principal) {
        WishlistResponse res = wishlistService.getWishlist(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Wishlist retrieved successfully", res));
    }

    @PostMapping("/{productId}")
    @Operation(summary = "Add Product to Wishlist", description = "Saves a product to user's wishlist.")
    public ResponseEntity<ApiResponse<WishlistResponse>> addToWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId) {
        WishlistResponse res = wishlistService.addToWishlist(principal.getId(), productId);
        return ResponseEntity.ok(ApiResponse.success("Product added to wishlist", res));
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove Product from Wishlist", description = "Removes a product from user's wishlist.")
    public ResponseEntity<ApiResponse<WishlistResponse>> removeFromWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId) {
        WishlistResponse res = wishlistService.removeFromWishlist(principal.getId(), productId);
        return ResponseEntity.ok(ApiResponse.success("Product removed from wishlist", res));
    }

    @DeleteMapping
    @Operation(summary = "Clear Wishlist", description = "Clears all items from user's wishlist.")
    public ResponseEntity<ApiResponse<String>> clearWishlist(@AuthenticationPrincipal UserPrincipal principal) {
        wishlistService.clearWishlist(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Wishlist cleared successfully", "OK"));
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "Check Wishlist Status", description = "Returns whether a product is currently wishlisted.")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkWishlist(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId) {
        boolean inWishlist = wishlistService.isProductInWishlist(principal.getId(), productId);
        return ResponseEntity.ok(ApiResponse.success("Status checked", Map.of("inWishlist", inWishlist)));
    }

    @PostMapping("/{productId}/move-to-cart")
    @Operation(summary = "Move Product to Cart", description = "Transfers an item from wishlist to shopping bag.")
    public ResponseEntity<ApiResponse<WishlistResponse>> moveToCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long productId,
            @RequestParam(required = false, defaultValue = "M") String size,
            @RequestParam(required = false, defaultValue = "Standard") String color) {
        WishlistResponse res = wishlistService.moveToCart(principal.getId(), productId, size, color);
        return ResponseEntity.ok(ApiResponse.success("Item moved to cart", res));
    }
}
