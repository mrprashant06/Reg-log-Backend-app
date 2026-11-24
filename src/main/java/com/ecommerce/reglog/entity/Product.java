package com.ecommerce.reglog.entity;

import com.ecommerce.reglog.enums.Gender;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_products_brand", columnList = "brand"),
    @Index(name = "idx_products_category", columnList = "category_id"),
    @Index(name = "idx_products_selling_price", columnList = "selling_price"),
    @Index(name = "idx_products_gender", columnList = "gender")
})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(length = 200)
    private String slug;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 100)
    private String brand;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "original_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal originalPrice;

    @Column(name = "selling_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "cost_price", precision = 12, scale = 2)
    private BigDecimal costPrice;

    @Column(name = "discount_percentage")
    private Integer discountPercentage = 0;

    @Column(name = "available_stock", nullable = false)
    private Integer availableStock = 0;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender = Gender.UNISEX;

    @Column(name = "rating")
    private Double rating = 4.5;

    @Column(name = "reviews_count")
    private Integer reviewsCount = 0;

    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductVariant> variants = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    public Product() {}

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) this.createdAt = Instant.now();
        if (this.updatedAt == null) this.updatedAt = Instant.now();
        if (this.isFeatured == null) this.isFeatured = false;
        if (this.isActive == null) this.isActive = true;
        if (this.rating == null) this.rating = 4.5;
        if (this.reviewsCount == null) this.reviewsCount = 0;
        calculateDiscount();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
        calculateDiscount();
    }

    public void calculateDiscount() {
        if (originalPrice != null && sellingPrice != null && originalPrice.compareTo(BigDecimal.ZERO) > 0) {
            if (sellingPrice.compareTo(originalPrice) < 0) {
                BigDecimal diff = originalPrice.subtract(sellingPrice);
                BigDecimal pct = diff.multiply(BigDecimal.valueOf(100)).divide(originalPrice, 0, java.math.RoundingMode.HALF_UP);
                this.discountPercentage = pct.intValue();
            } else {
                this.discountPercentage = 0;
            }
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public BigDecimal getCostPrice() {
        return costPrice != null ? costPrice : (sellingPrice != null ? sellingPrice.multiply(BigDecimal.valueOf(0.6)) : BigDecimal.ZERO);
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
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

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getReviewsCount() {
        return reviewsCount;
    }

    public void setReviewsCount(Integer reviewsCount) {
        this.reviewsCount = reviewsCount;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<ProductImage> getImages() {
        return images;
    }

    public void setImages(List<ProductImage> images) {
        this.images = images;
    }

    public List<ProductVariant> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariant> variants) {
        this.variants = variants;
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

    public String getPrimaryImageUrl() {
        if (images != null && !images.isEmpty()) {
            for (ProductImage img : images) {
                if (Boolean.TRUE.equals(img.getIsPrimary())) {
                    return img.getImageUrl();
                }
            }
            return images.get(0).getImageUrl();
        }
        return null;
    }

    public String getSecondaryImageUrl() {
        if (images != null && images.size() > 1) {
            String primary = getPrimaryImageUrl();
            for (ProductImage img : images) {
                if (!img.getImageUrl().equals(primary)) {
                    return img.getImageUrl();
                }
            }
            return images.get(1).getImageUrl();
        }
        return getPrimaryImageUrl();
    }
}
