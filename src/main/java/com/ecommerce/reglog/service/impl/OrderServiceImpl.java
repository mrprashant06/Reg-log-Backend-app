package com.ecommerce.reglog.service.impl;

import com.ecommerce.reglog.dto.request.OrderCancelRequest;
import com.ecommerce.reglog.dto.request.OrderFilterRequest;
import com.ecommerce.reglog.dto.request.OrderReturnRequest;
import com.ecommerce.reglog.dto.response.OrderResponse;
import com.ecommerce.reglog.entity.OrderItem;
import com.ecommerce.reglog.entity.Product;
import com.ecommerce.reglog.entity.PurchaseOrder;
import com.ecommerce.reglog.enums.OrderStatus;
import com.ecommerce.reglog.exception.BadRequestException;
import com.ecommerce.reglog.exception.ResourceNotFoundException;
import com.ecommerce.reglog.repository.OrderRepository;
import com.ecommerce.reglog.repository.ProductRepository;
import com.ecommerce.reglog.repository.spec.OrderSpecification;
import com.ecommerce.reglog.service.OrderService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderResponse> getUserOrders(Long userId, OrderFilterRequest request) {
        Sort sort = resolveSort(request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Specification<PurchaseOrder> spec = OrderSpecification.build(userId, request);
        return orderRepository.findAll(spec, pageable).map(OrderResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long userId, Long orderId) {
        PurchaseOrder order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return OrderResponse.fromEntity(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderNumber(Long userId, String orderNumber) {
        PurchaseOrder order = orderRepository.findByOrderNumberAndUserId(orderNumber, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "orderNumber", orderNumber));
        return OrderResponse.fromEntity(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(Long userId, Long orderId, OrderCancelRequest request) {
        PurchaseOrder order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (order.getOrderStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order is already cancelled");
        }

        if (order.getOrderStatus() == OrderStatus.SHIPPED ||
            order.getOrderStatus() == OrderStatus.OUT_FOR_DELIVERY ||
            order.getOrderStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Orders that have shipped or delivered cannot be cancelled. You can request a return after delivery.");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancellationReason(request.getReason());
        order.setUpdatedAt(Instant.now());

        // Restore product stock
        for (OrderItem item : order.getItems()) {
            if (item.getProduct() != null) {
                Product p = item.getProduct();
                p.setAvailableStock(p.getAvailableStock() + item.getQuantity());
                productRepository.save(p);
            }
        }

        PurchaseOrder updated = orderRepository.save(order);
        return OrderResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public OrderResponse requestReturn(Long userId, Long orderId, OrderReturnRequest request) {
        PurchaseOrder order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (order.getOrderStatus() != OrderStatus.DELIVERED) {
            throw new BadRequestException("Only delivered orders can be requested for return");
        }

        order.setOrderStatus(OrderStatus.RETURNED);
        order.setReturnReason(request.getReason());
        order.setUpdatedAt(Instant.now());

        PurchaseOrder updated = orderRepository.save(order);
        return OrderResponse.fromEntity(updated);
    }

    private Sort resolveSort(String sortBy) {
        if (sortBy == null) return Sort.by(Sort.Direction.DESC, "createdAt");
        switch (sortBy.toLowerCase().trim()) {
            case "oldest":
                return Sort.by(Sort.Direction.ASC, "createdAt");
            case "price_high":
            case "price_desc":
                return Sort.by(Sort.Direction.DESC, "totalAmount");
            case "price_low":
            case "price_asc":
                return Sort.by(Sort.Direction.ASC, "totalAmount");
            case "newest":
            default:
                return Sort.by(Sort.Direction.DESC, "createdAt");
        }
    }
}
