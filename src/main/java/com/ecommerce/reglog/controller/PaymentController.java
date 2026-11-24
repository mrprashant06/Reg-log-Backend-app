package com.ecommerce.reglog.controller;

import com.ecommerce.reglog.dto.request.RazorpayVerifyRequest;
import com.ecommerce.reglog.dto.response.ApiResponse;
import com.ecommerce.reglog.dto.response.OrderResponse;
import com.ecommerce.reglog.dto.response.RazorpayOrderResponse;
import com.ecommerce.reglog.security.UserPrincipal;
import com.ecommerce.reglog.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
@Tag(name = "Payment Processing", description = "Endpoints for Razorpay and Cash on Delivery payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/razorpay/create-order")
    @Operation(summary = "Create Razorpay Order", description = "Creates a Razorpay order via Razorpay SDK and returns razorpayOrderId.")
    public ResponseEntity<ApiResponse<RazorpayOrderResponse>> createRazorpayOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long orderId) {
        RazorpayOrderResponse response = paymentService.createRazorpayOrder(principal.getId(), orderId);
        return ResponseEntity.ok(ApiResponse.success("Razorpay order created", response));
    }

    @PostMapping("/razorpay/verify")
    @Operation(summary = "Verify Razorpay Payment Signature", description = "Verifies cryptographic HMAC signature before updating order to PAID.")
    public ResponseEntity<ApiResponse<OrderResponse>> verifyRazorpayPayment(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody RazorpayVerifyRequest request) {
        OrderResponse order = paymentService.verifyRazorpayPayment(principal.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Payment verified successfully", order));
    }

    @PostMapping("/cod")
    @Operation(summary = "Process Cash on Delivery Payment")
    public ResponseEntity<ApiResponse<OrderResponse>> processCodPayment(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam Long orderId) {
        OrderResponse order = paymentService.processCodPayment(principal.getId(), orderId);
        return ResponseEntity.ok(ApiResponse.success("Cash on Delivery confirmed", order));
    }
}
