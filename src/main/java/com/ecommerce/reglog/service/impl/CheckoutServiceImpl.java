package com.ecommerce.reglog.service.impl;

import com.ecommerce.reglog.dto.request.PlaceOrderRequest;
import com.ecommerce.reglog.dto.response.CartResponse;
import com.ecommerce.reglog.dto.response.OrderResponse;
import com.ecommerce.reglog.entity.*;
import com.ecommerce.reglog.enums.OrderStatus;
import com.ecommerce.reglog.enums.PaymentMethod;
import com.ecommerce.reglog.enums.PaymentStatus;
import com.ecommerce.reglog.exception.BadRequestException;
import com.ecommerce.reglog.exception.ResourceNotFoundException;
import com.ecommerce.reglog.repository.*;
import com.ecommerce.reglog.service.CartService;
import com.ecommerce.reglog.service.CheckoutService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class CheckoutServiceImpl implements CheckoutService {

    private static final SecureRandom random = new SecureRandom();
    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("999.00");
    private static final BigDecimal STANDARD_DELIVERY_CHARGE = new BigDecimal("99.00");

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final CartService cartService;

    public CheckoutServiceImpl(UserRepository userRepository,
                               AddressRepository addressRepository,
                               CartRepository cartRepository,
                               CartItemRepository cartItemRepository,
                               ProductRepository productRepository,
                               ProductVariantRepository variantRepository,
                               OrderRepository orderRepository,
                               PaymentRepository paymentRepository,
                               CartService cartService) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.cartService = cartService;
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse validateCheckout(Long userId, Long addressId) {
        if (addressId != null) {
            addressRepository.findByIdAndUserId(addressId, userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address", "id", addressId));
        }
        return cartService.getCartForUser(userId);
    }

    @Override
    @Transactional
    public OrderResponse placeOrder(Long userId, PlaceOrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        Address address = addressRepository.findByIdAndUserId(request.getAddressId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", request.getAddressId()));

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new BadRequestException("No active shopping cart found"));

        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new BadRequestException("Shopping cart is empty. Please add items before placing order.");
        }

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalOriginal = BigDecimal.ZERO;

        // Verify stock and calculate server-side totals
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            if (!Boolean.TRUE.equals(product.getIsActive())) {
                throw new BadRequestException("Item '" + product.getName() + "' is no longer active");
            }

            int available = product.getAvailableStock();
            if (item.getVariant() != null && item.getVariant().getStockQuantity() != null) {
                available = item.getVariant().getStockQuantity();
            }

            if (item.getQuantity() > available) {
                throw new BadRequestException("Item '" + product.getName() + "' has insufficient stock (Available: " + available + ")");
            }

            BigDecimal unitPrice = product.getSellingPrice();
            if (item.getVariant() != null && item.getVariant().getAdditionalPrice() != null) {
                unitPrice = unitPrice.add(item.getVariant().getAdditionalPrice());
            }

            subtotal = subtotal.add(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())));
            totalOriginal = totalOriginal.add(product.getOriginalPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        BigDecimal deliveryCharge = subtotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0 ? BigDecimal.ZERO : STANDARD_DELIVERY_CHARGE;
        BigDecimal discountAmount = totalOriginal.compareTo(subtotal) > 0 ? totalOriginal.subtract(subtotal) : BigDecimal.ZERO;
        BigDecimal totalAmount = subtotal.add(deliveryCharge);

        // Generate Order Number
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randSuffix = String.format("%04d", random.nextInt(10000));
        String orderNumber = "AURA-" + dateStr + "-" + randSuffix;

        // Create PurchaseOrder
        PurchaseOrder order = new PurchaseOrder();
        order.setOrderNumber(orderNumber);
        order.setUser(user);
        order.setOrderStatus(OrderStatus.ORDER_PLACED);
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discountAmount);
        order.setDeliveryCharge(deliveryCharge);
        order.setTotalAmount(totalAmount);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus(request.getPaymentMethod() == PaymentMethod.COD ? PaymentStatus.PENDING : PaymentStatus.PENDING);

        // Snapshot address details
        order.setShippingFullName(address.getFullName() != null ? address.getFullName() : user.getFullName());
        order.setShippingMobileNumber(address.getMobileNumber() != null ? address.getMobileNumber() : user.getPhone());
        order.setShippingStreetAddress(address.getStreetAddress());
        order.setShippingApartmentSuite(address.getApartmentSuite());
        order.setShippingCity(address.getCity());
        order.setShippingState(address.getState());
        order.setShippingPostalCode(address.getPostalCode());
        order.setShippingCountry(address.getCountry());
        order.setShippingAddressType(address.getAddressType().name());
        order.setEstimatedDeliveryInfo("Estimated Delivery in 3-5 business days");
        order.setNotes(request.getNotes());
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(Instant.now());

        PurchaseOrder savedOrder = orderRepository.save(order);

        // Create Order Items and update inventory
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cartItems) {
            Product product = item.getProduct();
            BigDecimal unitPrice = product.getSellingPrice();
            if (item.getVariant() != null && item.getVariant().getAdditionalPrice() != null) {
                unitPrice = unitPrice.add(item.getVariant().getAdditionalPrice());
            }

            OrderItem orderItem = new OrderItem(
                    savedOrder,
                    product,
                    product.getName(),
                    product.getBrand(),
                    product.getPrimaryImageUrl(),
                    item.getSelectedSize(),
                    item.getSelectedColor(),
                    unitPrice,
                    item.getQuantity()
            );
            orderItems.add(orderItem);

            // Deduct stock
            product.setAvailableStock(Math.max(0, product.getAvailableStock() - item.getQuantity()));
            productRepository.save(product);

            if (item.getVariant() != null) {
                ProductVariant var = item.getVariant();
                var.setStockQuantity(Math.max(0, var.getStockQuantity() - item.getQuantity()));
                variantRepository.save(var);
            }
        }
        savedOrder.setItems(orderItems);
        orderRepository.save(savedOrder);

        // Create initial Payment record
        Payment payment = new Payment();
        payment.setOrder(savedOrder);
        payment.setUser(user);
        payment.setPaymentMethod(request.getPaymentMethod().name());
        payment.setAmount(totalAmount);
        payment.setCurrency("INR");
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        // Empty user cart after order creation
        cartItemRepository.deleteByCartId(cart.getId());

        return OrderResponse.fromEntity(savedOrder);
    }
}
