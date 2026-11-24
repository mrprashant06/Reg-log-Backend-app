package com.ecommerce.reglog.entity;

import com.ecommerce.reglog.enums.OrderStatus;
import com.ecommerce.reglog.enums.PaymentMethod;
import com.ecommerce.reglog.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_orders", indexes = {
    @Index(name = "idx_orders_user", columnList = "user_id"),
    @Index(name = "idx_orders_status", columnList = "order_status"),
    @Index(name = "idx_orders_created", columnList = "created_at"),
    @Index(name = "idx_orders_order_number", columnList = "order_number")
})
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, unique = true, length = 64)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", nullable = false, length = 30)
    private OrderStatus orderStatus = OrderStatus.ORDER_PLACED;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "delivery_charge", precision = 12, scale = 2)
    private BigDecimal deliveryCharge = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, length = 30)
    private PaymentMethod paymentMethod = PaymentMethod.COD;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 30)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    // Delivery address snapshot
    @Column(name = "shipping_full_name", nullable = false, length = 100)
    private String shippingFullName;

    @Column(name = "shipping_mobile_number", nullable = false, length = 20)
    private String shippingMobileNumber;

    @Column(name = "shipping_street_address", nullable = false, length = 255)
    private String shippingStreetAddress;

    @Column(name = "shipping_apartment_suite", length = 100)
    private String shippingApartmentSuite;

    @Column(name = "shipping_city", nullable = false, length = 100)
    private String shippingCity;

    @Column(name = "shipping_state", nullable = false, length = 100)
    private String shippingState;

    @Column(name = "shipping_postal_code", nullable = false, length = 20)
    private String shippingPostalCode;

    @Column(name = "shipping_country", nullable = false, length = 100)
    private String shippingCountry = "India";

    @Column(name = "shipping_address_type", length = 30)
    private String shippingAddressType = "HOME";

    @Column(name = "estimated_delivery_info", length = 150)
    private String estimatedDeliveryInfo;

    @Column(length = 500)
    private String notes;

    @Column(name = "cancellation_reason", length = 300)
    private String cancellationReason;

    @Column(name = "return_reason", length = 300)
    private String returnReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public PurchaseOrder() {}

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = Instant.now();
        if (this.updatedAt == null) this.updatedAt = Instant.now();
        if (this.orderStatus == null) this.orderStatus = OrderStatus.ORDER_PLACED;
        if (this.paymentStatus == null) this.paymentStatus = PaymentStatus.PENDING;
        if (this.subtotal == null) this.subtotal = BigDecimal.ZERO;
        if (this.discountAmount == null) this.discountAmount = BigDecimal.ZERO;
        if (this.deliveryCharge == null) this.deliveryCharge = BigDecimal.ZERO;
        if (this.totalAmount == null) this.totalAmount = BigDecimal.ZERO;
        if (this.estimatedDeliveryInfo == null) {
            this.estimatedDeliveryInfo = "Estimated Delivery in 3-5 business days";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
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

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
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
}
