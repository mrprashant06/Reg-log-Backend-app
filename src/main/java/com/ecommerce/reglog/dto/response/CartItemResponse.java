package com.ecommerce.reglog.dto.response;

import com.ecommerce.reglog.entity.CartItem;
import java.math.BigDecimal;

public class CartItemResponse {

    private Long id;
    private Long productId;
    private String productName;
    private String productBrand;
    private String productImageUrl;
    private Long variantId;
    private String selectedSize;
    private String selectedColor;
    private BigDecimal originalPrice;
    private BigDecimal sellingPrice;
    private Integer discountPercentage;
    private Integer availableStock;
    private Integer quantity;
    private BigDecimal subtotal;

    public CartItemResponse() {}

    public static CartItemResponse fromEntity(CartItem item) {
        CartItemResponse res = new CartItemResponse();
        res.setId(item.getId());
        if (item.getProduct() != null) {
            res.setProductId(item.getProduct().getId());
            res.setProductName(item.getProduct().getName());
            res.setProductBrand(item.getProduct().getBrand());
            res.setProductImageUrl(item.getProduct().getPrimaryImageUrl());
            res.setOriginalPrice(item.getProduct().getOriginalPrice());
            res.setSellingPrice(item.getProduct().getSellingPrice());
            res.setDiscountPercentage(item.getProduct().getDiscountPercentage());
            res.setAvailableStock(item.getProduct().getAvailableStock());

            BigDecimal price = item.getProduct().getSellingPrice();
            if (item.getVariant() != null && item.getVariant().getAdditionalPrice() != null) {
                price = price.add(item.getVariant().getAdditionalPrice());
                res.setSellingPrice(price);
            }
            res.setQuantity(item.getQuantity());
            res.setSubtotal(price.multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        if (item.getVariant() != null) {
            res.setVariantId(item.getVariant().getId());
            if (item.getVariant().getStockQuantity() != null) {
                res.setAvailableStock(item.getVariant().getStockQuantity());
            }
        }
        res.setSelectedSize(item.getSelectedSize());
        res.setSelectedColor(item.getSelectedColor());
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

    public Long getVariantId() {
        return variantId;
    }

    public void setVariantId(Long variantId) {
        this.variantId = variantId;
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

    public BigDecimal getOriginalPrice() {
        return originalPrice;
    }

    public void setOriginalPrice(BigDecimal originalPrice) {
        this.originalPrice = originalPrice;
    }

    public BigDecimal getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(BigDecimal sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Integer getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Integer discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Integer getAvailableStock() {
        return availableStock;
    }

    public void setAvailableStock(Integer availableStock) {
        this.availableStock = availableStock;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
