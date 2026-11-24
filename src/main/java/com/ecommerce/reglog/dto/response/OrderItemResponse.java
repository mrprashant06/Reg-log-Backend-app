package com.ecommerce.reglog.dto.response;

import com.ecommerce.reglog.entity.OrderItem;
import java.math.BigDecimal;

public class OrderItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productBrand;
    private String productImageUrl;
    private String selectedSize;
    private String selectedColor;
    private BigDecimal unitPrice;
    private Integer quantity;
    private BigDecimal totalPrice;

    public OrderItemResponse() {}

    public static OrderItemResponse fromEntity(OrderItem item) {
        OrderItemResponse res = new OrderItemResponse();
        res.setId(item.getId());
        if (item.getProduct() != null) {
            res.setProductId(item.getProduct().getId());
        }
        res.setProductName(item.getProductName());
        res.setProductBrand(item.getProductBrand());
        res.setProductImageUrl(item.getProductImageUrl());
        res.setSelectedSize(item.getSelectedSize());
        res.setSelectedColor(item.getSelectedColor());
        res.setUnitPrice(item.getUnitPrice());
        res.setQuantity(item.getQuantity());
        res.setTotalPrice(item.getTotalPrice());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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
}
