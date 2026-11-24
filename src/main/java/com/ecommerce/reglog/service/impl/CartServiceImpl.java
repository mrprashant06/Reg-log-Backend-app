package com.ecommerce.reglog.service.impl;

import com.ecommerce.reglog.dto.request.AddToCartRequest;
import com.ecommerce.reglog.dto.request.UpdateCartItemRequest;
import com.ecommerce.reglog.dto.response.CartItemResponse;
import com.ecommerce.reglog.dto.response.CartResponse;
import com.ecommerce.reglog.entity.*;
import com.ecommerce.reglog.exception.BadRequestException;
import com.ecommerce.reglog.exception.ResourceNotFoundException;
import com.ecommerce.reglog.repository.*;
import com.ecommerce.reglog.service.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private static final BigDecimal FREE_DELIVERY_THRESHOLD = new BigDecimal("999.00");
    private static final BigDecimal STANDARD_DELIVERY_CHARGE = new BigDecimal("99.00");

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           ProductVariantRepository variantRepository,
                           UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.variantRepository = variantRepository;
        this.userRepository = userRepository;
    }

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
            Cart newCart = new Cart(user);
            return cartRepository.save(newCart);
        });
    }

    @Override
    @Transactional
    public CartResponse getCartForUser(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return buildCartResponse(cart);
    }

    @Override
    @Transactional
    public CartResponse addToCart(Long userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", request.getProductId()));

        if (!Boolean.TRUE.equals(product.getIsActive())) {
            throw new BadRequestException("This product is currently unavailable");
        }

        ProductVariant variant = null;
        int maxStock = product.getAvailableStock() != null ? product.getAvailableStock() : 0;

        if (request.getVariantId() != null) {
            variant = variantRepository.findById(request.getVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("ProductVariant", "id", request.getVariantId()));
            if (variant.getStockQuantity() != null) {
                maxStock = variant.getStockQuantity();
            }
        }

        if (maxStock <= 0) {
            throw new BadRequestException("Product variant is out of stock");
        }

        String size = request.getSelectedSize() != null ? request.getSelectedSize().trim() : null;
        String color = request.getSelectedColor() != null ? request.getSelectedColor().trim() : null;

        // Check for existing variant in cart
        Optional<CartItem> existingItem = cartItemRepository
                .findByCartIdAndProductIdAndSelectedSizeAndSelectedColor(cart.getId(), product.getId(), size, color);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            int newQuantity = item.getQuantity() + request.getQuantity();
            if (newQuantity > maxStock) {
                throw new BadRequestException("Cannot add more. Available stock is " + maxStock);
            }
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        } else {
            if (request.getQuantity() > maxStock) {
                throw new BadRequestException("Requested quantity (" + request.getQuantity() + ") exceeds available stock (" + maxStock + ")");
            }
            CartItem newItem = new CartItem(cart, product, variant, size, color, request.getQuantity());
            cartItemRepository.save(newItem);
        }

        // Reload updated cart
        Cart updatedCart = cartRepository.findById(cart.getId()).orElse(cart);
        return buildCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartResponse updateCartItem(Long userId, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to the user's cart");
        }

        if (request.getQuantity() <= 0) {
            cartItemRepository.delete(item);
        } else {
            int maxStock = item.getProduct().getAvailableStock();
            if (item.getVariant() != null && item.getVariant().getStockQuantity() != null) {
                maxStock = item.getVariant().getStockQuantity();
            }
            if (request.getQuantity() > maxStock) {
                throw new BadRequestException("Requested quantity (" + request.getQuantity() + ") exceeds available stock (" + maxStock + ")");
            }
            item.setQuantity(request.getQuantity());
            cartItemRepository.save(item);
        }

        Cart updatedCart = cartRepository.findById(cart.getId()).orElse(cart);
        return buildCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public CartResponse removeCartItem(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", itemId));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Cart item does not belong to the user's cart");
        }

        cartItemRepository.delete(item);

        Cart updatedCart = cartRepository.findById(cart.getId()).orElse(cart);
        return buildCartResponse(updatedCart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        cartItemRepository.deleteByCartId(cart.getId());
    }

    private CartResponse buildCartResponse(Cart cart) {
        CartResponse res = new CartResponse();
        res.setCartId(cart.getId());

        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
        List<CartItemResponse> itemResponses = new ArrayList<>();

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalOriginal = BigDecimal.ZERO;
        int totalItems = 0;

        for (CartItem item : items) {
            CartItemResponse ir = CartItemResponse.fromEntity(item);
            itemResponses.add(ir);

            if (ir.getSubtotal() != null) {
                subtotal = subtotal.add(ir.getSubtotal());
            }
            if (ir.getOriginalPrice() != null) {
                totalOriginal = totalOriginal.add(ir.getOriginalPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
            totalItems += item.getQuantity();
        }

        res.setItems(itemResponses);
        res.setTotalItems(totalItems);
        res.setSubtotal(subtotal);

        BigDecimal discount = BigDecimal.ZERO;
        if (totalOriginal.compareTo(subtotal) > 0) {
            discount = totalOriginal.subtract(subtotal);
        }
        res.setDiscountAmount(discount);

        if (totalItems == 0) {
            res.setDeliveryCharge(BigDecimal.ZERO);
            res.setFinalPayableAmount(BigDecimal.ZERO);
            res.setFreeDeliveryEligible(false);
        } else {
            boolean isFree = subtotal.compareTo(FREE_DELIVERY_THRESHOLD) >= 0;
            res.setFreeDeliveryEligible(isFree);
            BigDecimal delivery = isFree ? BigDecimal.ZERO : STANDARD_DELIVERY_CHARGE;
            res.setDeliveryCharge(delivery);
            res.setFinalPayableAmount(subtotal.add(delivery));
        }

        return res;
    }
}
