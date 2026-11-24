package com.ecommerce.reglog.controller;

import com.ecommerce.reglog.dto.request.OrderCancelRequest;
import com.ecommerce.reglog.dto.request.OrderFilterRequest;
import com.ecommerce.reglog.dto.request.OrderReturnRequest;
import com.ecommerce.reglog.dto.response.ApiResponse;
import com.ecommerce.reglog.dto.response.OrderResponse;
import com.ecommerce.reglog.dto.response.PageResponse;
import com.ecommerce.reglog.enums.OrderStatus;
import com.ecommerce.reglog.enums.PaymentMethod;
import com.ecommerce.reglog.security.UserPrincipal;
import com.ecommerce.reglog.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
@Tag(name = "Order History & Tracking", description = "Endpoints for user order management, status tracking, cancellations, and returns")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @Operation(summary = "Get User Orders", description = "Retrieves user orders with flexible search, status filtering, date range, and sorting.")
    public ResponseEntity<ApiResponse<PageResponse<OrderResponse>>> getUserOrders(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) PaymentMethod paymentMethod,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "newest") String sortBy,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        OrderFilterRequest req = new OrderFilterRequest();
        req.setQuery(query);
        req.setOrderStatus(status);
        req.setPaymentMethod(paymentMethod);
        req.setStartDate(startDate);
        req.setEndDate(endDate);
        req.setSortBy(sortBy);
        req.setPage(page);
        req.setSize(size);

        Page<OrderResponse> result = orderService.getUserOrders(principal.getId(), req);
        PageResponse<OrderResponse> pageResponse = new PageResponse<>(
                result.getContent(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isLast()
        );

        return ResponseEntity.ok(ApiResponse.success("Orders retrieved", pageResponse));
    }

    @GetMapping("/{orderId}")
    @Operation(summary = "Get Order Details by ID")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderById(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long orderId) {
        OrderResponse order = orderService.getOrderById(principal.getId(), orderId);
        return ResponseEntity.ok(ApiResponse.success("Order details retrieved", order));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get Order Details by Order Number")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrderByOrderNumber(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable String orderNumber) {
        OrderResponse order = orderService.getOrderByOrderNumber(principal.getId(), orderNumber);
        return ResponseEntity.ok(ApiResponse.success("Order details retrieved", order));
    }

    @PutMapping("/{orderId}/cancel")
    @Operation(summary = "Cancel Eligible Order", description = "Cancels order if not yet shipped and restores inventory.")
    public ResponseEntity<ApiResponse<OrderResponse>> cancelOrder(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long orderId,
            @Valid @RequestBody OrderCancelRequest request) {
        OrderResponse order = orderService.cancelOrder(principal.getId(), orderId, request);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", order));
    }

    @PostMapping("/{orderId}/return")
    @Operation(summary = "Request Return for Delivered Order")
    public ResponseEntity<ApiResponse<OrderResponse>> requestReturn(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long orderId,
            @Valid @RequestBody OrderReturnRequest request) {
        OrderResponse order = orderService.requestReturn(principal.getId(), orderId, request);
        return ResponseEntity.ok(ApiResponse.success("Return request submitted successfully", order));
    }
}
