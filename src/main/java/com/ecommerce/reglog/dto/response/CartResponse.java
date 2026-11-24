package com.ecommerce.reglog.dto.response;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CartResponse {

    private Long cartId;
    private List<CartItemResponse> items = new ArrayList<>();
    private int totalItems = 0;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal discountAmount = BigDecimal.ZERO;
    private BigDecimal deliveryCharge = BigDecimal.ZERO;
    private BigDecimal finalPayableAmount = BigDecimal.ZERO;
    private boolean freeDeliveryEligible = false;
    private BigDecimal freeDeliveryThreshold = new BigDecimal("999.00");

    public CartResponse() {}

    public Long getCartId() {
        return cartId;
    }

    public void setCartId(Long cartId) {
        this.cartId = cartId;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public void setItems(List<CartItemResponse> items) {
        this.items = items;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(BigDecimal discountAmount) {
        this.discountAmount = discountAmount;
    }

    public BigDecimal getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(BigDecimal deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public BigDecimal getFinalPayableAmount() {
        return finalPayableAmount;
    }

    public void setFinalPayableAmount(BigDecimal finalPayableAmount) {
        this.finalPayableAmount = finalPayableAmount;
    }

    public boolean isFreeDeliveryEligible() {
        return freeDeliveryEligible;
    }

    public void setFreeDeliveryEligible(boolean freeDeliveryEligible) {
        this.freeDeliveryEligible = freeDeliveryEligible;
    }

    public BigDecimal getFreeDeliveryThreshold() {
        return freeDeliveryThreshold;
    }

    public void setFreeDeliveryThreshold(BigDecimal freeDeliveryThreshold) {
        this.freeDeliveryThreshold = freeDeliveryThreshold;
    }
}
