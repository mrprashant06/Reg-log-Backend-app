package com.ecommerce.reglog.dto.response;

import com.ecommerce.reglog.entity.PurchaseOrder;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponse {

    private Long id;
    private String orderNumber;
    private String orderStatus;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal deliveryCharge;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private String paymentStatus;

    // Shipping address snapshot
    private String shippingFullName;
    private String shippingMobileNumber;
    private String shippingStreetAddress;
    private String shippingApartmentSuite;
    private String shippingCity;
    private String shippingState;
    private String shippingPostalCode;
    private String shippingCountry;
    private String shippingAddressType;
    private String estimatedDeliveryInfo;
    private String cancellationReason;
    private String returnReason;
    private Instant createdAt;
    private Instant updatedAt;

    private List<OrderItemResponse> items = new ArrayList<>();

    public OrderResponse() {}

    public static OrderResponse fromEntity(PurchaseOrder o) {
        OrderResponse res = new OrderResponse();
        res.setId(o.getId());
        res.setOrderNumber(o.getOrderNumber());
        res.setOrderStatus(o.getOrderStatus() != null ? o.getOrderStatus().name() : "ORDER_PLACED");
        res.setSubtotal(o.getSubtotal());
        res.setDiscountAmount(o.getDiscountAmount());
        res.setDeliveryCharge(o.getDeliveryCharge());
        res.setTotalAmount(o.getTotalAmount());
        res.setPaymentMethod(o.getPaymentMethod() != null ? o.getPaymentMethod().name() : "COD");
        res.setPaymentStatus(o.getPaymentStatus() != null ? o.getPaymentStatus().name() : "PENDING");

        res.setShippingFullName(o.getShippingFullName());
        res.setShippingMobileNumber(o.getShippingMobileNumber());
        res.setShippingStreetAddress(o.getShippingStreetAddress());
        res.setShippingApartmentSuite(o.getShippingApartmentSuite());
        res.setShippingCity(o.getShippingCity());
        res.setShippingState(o.getShippingState());
        res.setShippingPostalCode(o.getShippingPostalCode());
        res.setShippingCountry(o.getShippingCountry());
        res.setShippingAddressType(o.getShippingAddressType());
        res.setEstimatedDeliveryInfo(o.getEstimatedDeliveryInfo());
        res.setCancellationReason(o.getCancellationReason());
        res.setReturnReason(o.getReturnReason());
        res.setCreatedAt(o.getCreatedAt());
        res.setUpdatedAt(o.getUpdatedAt());

        if (o.getItems() != null) {
            res.setItems(o.getItems().stream()
                    .map(OrderItemResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getShippingFullName() {
        return shippingFullName;
    }

    public void setShippingFullName(String shippingFullName) {
        this.shippingFullName = shippingFullName;
    }

    public String getShippingMobileNumber() {
        return shippingMobileNumber;
    }

    public void setShippingMobileNumber(String shippingMobileNumber) {
        this.shippingMobileNumber = shippingMobileNumber;
    }

    public String getShippingStreetAddress() {
        return shippingStreetAddress;
    }

    public void setShippingStreetAddress(String shippingStreetAddress) {
        this.shippingStreetAddress = shippingStreetAddress;
    }

    public String getShippingApartmentSuite() {
        return shippingApartmentSuite;
    }

    public void setShippingApartmentSuite(String shippingApartmentSuite) {
        this.shippingApartmentSuite = shippingApartmentSuite;
    }

    public String getShippingCity() {
        return shippingCity;
    }

    public void setShippingCity(String shippingCity) {
        this.shippingCity = shippingCity;
    }

    public String getShippingState() {
        return shippingState;
    }

    public void setShippingState(String shippingState) {
        this.shippingState = shippingState;
    }

    public String getShippingPostalCode() {
        return shippingPostalCode;
    }

    public void setShippingPostalCode(String shippingPostalCode) {
        this.shippingPostalCode = shippingPostalCode;
    }

    public String getShippingCountry() {
        return shippingCountry;
    }

    public void setShippingCountry(String shippingCountry) {
        this.shippingCountry = shippingCountry;
    }

    public String getShippingAddressType() {
        return shippingAddressType;
    }

    public void setShippingAddressType(String shippingAddressType) {
        this.shippingAddressType = shippingAddressType;
    }

    public String getEstimatedDeliveryInfo() {
        return estimatedDeliveryInfo;
    }

    public void setEstimatedDeliveryInfo(String estimatedDeliveryInfo) {
        this.estimatedDeliveryInfo = estimatedDeliveryInfo;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getReturnReason() {
        return returnReason;
    }

    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }
}
