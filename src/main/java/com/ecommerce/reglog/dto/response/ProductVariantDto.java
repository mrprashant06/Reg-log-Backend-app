package com.ecommerce.reglog.dto.response;

import com.ecommerce.reglog.entity.ProductVariant;
import java.math.BigDecimal;

public class ProductVariantDto {

    private Long id;
    private String size;
    private String color;
    private String colorHex;
    private String sku;
    private Integer stockQuantity;
    private BigDecimal additionalPrice;

    public ProductVariantDto() {}

    public static ProductVariantDto fromEntity(ProductVariant v) {
        ProductVariantDto dto = new ProductVariantDto();
        dto.setId(v.getId());
        dto.setSize(v.getSize());
        dto.setColor(v.getColor());
        dto.setColorHex(v.getColorHex());
        dto.setSku(v.getSku());
        dto.setStockQuantity(v.getStockQuantity());
        dto.setAdditionalPrice(v.getAdditionalPrice());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getColorHex() {
        return colorHex;
    }

    public void setColorHex(String colorHex) {
        this.colorHex = colorHex;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public BigDecimal getAdditionalPrice() {
        return additionalPrice;
    }

    public void setAdditionalPrice(BigDecimal additionalPrice) {
        this.additionalPrice = additionalPrice;
    }
}
