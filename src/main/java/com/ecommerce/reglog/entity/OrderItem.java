package com.ecommerce.reglog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnore
    private PurchaseOrder order;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "product_name", nullable = false, length = 200)
    private String productName;

    @Column(name = "product_brand", length = 100)
    private String productBrand;

    @Column(name = "product_image_url", length = 600)
    private String productImageUrl;

    @Column(name = "selected_size", length = 50)
    private String selectedSize;

    @Column(name = "selected_color", length = 50)
    private String selectedColor;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "unit_cost_price", precision = 12, scale = 2)
    private BigDecimal unitCostPrice = BigDecimal.ZERO;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    public OrderItem() {}

    public OrderItem(PurchaseOrder order, Product product, String productName, String productBrand,
                     String productImageUrl, String selectedSize, String selectedColor,
                     BigDecimal unitPrice, Integer quantity) {
        this.order = order;
        this.product = product;
        this.productName = productName;
        this.productBrand = productBrand;
        this.productImageUrl = productImageUrl;
        this.selectedSize = selectedSize;
        this.selectedColor = selectedColor;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        this.unitCostPrice = product != null && product.getCostPrice() != null ? product.getCostPrice() : this.unitPrice.multiply(BigDecimal.valueOf(0.6));
        this.quantity = quantity != null ? quantity : 1;
        this.totalPrice = this.unitPrice.multiply(BigDecimal.valueOf(this.quantity));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PurchaseOrder getOrder() {
        return order;
    }

    public void setOrder(PurchaseOrder order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public String getSelectedSize() {
        return selectedSize;
    }

    public void setSelectedSize(String selectedSize) {
        this.selectedSize = selectedSize;
    }

    public String getSelectedColor() {
        return selectedColor;
    }

    public void setSelectedColor(String selectedColor) {
        this.selectedColor = selectedColor;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BigDecimal getUnitCostPrice() {
        return unitCostPrice != null ? unitCostPrice : unitPrice.multiply(BigDecimal.valueOf(0.6));
    }

    public void setUnitCostPrice(BigDecimal unitCostPrice) {
        this.unitCostPrice = unitCostPrice;
    }
}
