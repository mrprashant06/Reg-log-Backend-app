package com.ecommerce.reglog.service;

import com.ecommerce.reglog.dto.request.OrderCancelRequest;
import com.ecommerce.reglog.dto.request.OrderFilterRequest;
import com.ecommerce.reglog.dto.request.OrderReturnRequest;
import com.ecommerce.reglog.dto.response.OrderResponse;
import org.springframework.data.domain.Page;

public interface OrderService {
    Page<OrderResponse> getUserOrders(Long userId, OrderFilterRequest request);
    OrderResponse getOrderById(Long userId, Long orderId);
    OrderResponse getOrderByOrderNumber(Long userId, String orderNumber);
    OrderResponse cancelOrder(Long userId, Long orderId, OrderCancelRequest request);
    OrderResponse requestReturn(Long userId, Long orderId, OrderReturnRequest request);
}
