package com.ecommerce.reglog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variants")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @Column(length = 50)
    private String size; // e.g. "S", "M", "L", "XL", "UK 8", "UK 9", "Free Size"

    @Column(length = 50)
    private String color; // e.g. "Black", "White", "Blue", "Red", "Green"

    @Column(name = "color_hex", length = 20)
    private String colorHex; // e.g. "#000000"

    @Column(length = 100)
    private String sku;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "additional_price", precision = 10, scale = 2)
    private BigDecimal additionalPrice = BigDecimal.ZERO;

    public ProductVariant() {}

    public ProductVariant(Product product, String size, String color, String colorHex, Integer stockQuantity) {
        this.product = product;
        this.size = size;
        this.color = color;
        this.colorHex = colorHex;
        this.stockQuantity = stockQuantity != null ? stockQuantity : 0;
        this.additionalPrice = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
