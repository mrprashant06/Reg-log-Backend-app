package com.ecommerce.reglog.dto.response;

import java.math.BigDecimal;

public class RazorpayOrderResponse {

    private String razorpayOrderId;
    private Long orderId;
    private String orderNumber;
    private BigDecimal amount;
    private String currency;
    private String keyId; // Razorpay public key ID for React checkout

    public RazorpayOrderResponse() {}

    public RazorpayOrderResponse(String razorpayOrderId, Long orderId, String orderNumber, BigDecimal amount, String currency, String keyId) {
        this.razorpayOrderId = razorpayOrderId;
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.amount = amount;
        this.currency = currency;
        this.keyId = keyId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }
}
