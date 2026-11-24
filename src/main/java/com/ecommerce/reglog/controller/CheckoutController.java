package com.ecommerce.reglog.controller;

import com.ecommerce.reglog.dto.request.PlaceOrderRequest;
import com.ecommerce.reglog.dto.response.ApiResponse;
import com.ecommerce.reglog.dto.response.CartResponse;
import com.ecommerce.reglog.dto.response.OrderResponse;
import com.ecommerce.reglog.security.UserPrincipal;
import com.ecommerce.reglog.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@CrossOrigin(origins = "*")
@Tag(name = "Checkout Processing", description = "Endpoints for order validation and placement")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate Checkout", description = "Validates inventory, prices, and address before placing order.")
    public ResponseEntity<ApiResponse<CartResponse>> validateCheckout(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Long addressId) {
        CartResponse cart = checkoutService.validateCheckout(principal.getId(), addressId);
        return ResponseEntity.ok(ApiResponse.success("Checkout validated", cart));
    }

    @PostMapping("/place-order")
    @Operation(summary = "Place Order", description = "Validates stock, creates order and order items, updates inventory, and initializes payment.")
    public ResponseEntity<ApiResponse<OrderResponse>> placeOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody PlaceOrderRequest request) {
        OrderResponse order = checkoutService.placeOrder(principal.getId(), request);
        return new ResponseEntity<>(ApiResponse.success("Order placed successfully", order), HttpStatus.CREATED);
    }
}
