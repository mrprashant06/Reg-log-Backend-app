package com.ecommerce.reglog.controller;

import com.ecommerce.reglog.dto.request.AddToCartRequest;
import com.ecommerce.reglog.dto.request.UpdateCartItemRequest;
import com.ecommerce.reglog.dto.response.ApiResponse;
import com.ecommerce.reglog.dto.response.CartResponse;
import com.ecommerce.reglog.security.UserPrincipal;
import com.ecommerce.reglog.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
@Tag(name = "Shopping Cart", description = "Endpoints for managing user shopping cart and calculations")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Get User Cart", description = "Returns active cart with itemized subtotal, discounts, and delivery charges.")
    public ResponseEntity<ApiResponse<CartResponse>> getCart(@AuthenticationPrincipal UserPrincipal principal) {
        CartResponse cart = cartService.getCartForUser(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved", cart));
    }

    @PostMapping("/items")
    @Operation(summary = "Add Item to Cart", description = "Adds a product variant to the cart or increments quantity.")
    public ResponseEntity<ApiResponse<CartResponse>> addToCart(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody AddToCartRequest request) {
        CartResponse cart = cartService.addToCart(principal.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Item added to cart", cart), HttpStatus.CREATED);
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update Cart Item Quantity", description = "Sets new quantity or removes if quantity is 0.")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        CartResponse cart = cartService.updateCartItem(principal.getId(), itemId, request);
        return ResponseEntity.ok(ApiResponse.success("Cart item updated", cart));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove Item from Cart")
    public ResponseEntity<ApiResponse<CartResponse>> removeCartItem(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long itemId) {
        CartResponse cart = cartService.removeCartItem(principal.getId(), itemId);
        return ResponseEntity.ok(ApiResponse.success("Item removed from cart", cart));
    }

    @DeleteMapping
    @Operation(summary = "Clear Cart")
    public ResponseEntity<ApiResponse<Void>> clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        cartService.clearCart(principal.getId());
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully"));
    }
}
