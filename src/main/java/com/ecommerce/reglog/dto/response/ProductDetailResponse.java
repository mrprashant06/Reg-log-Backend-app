package com.ecommerce.reglog.dto.response;

import com.ecommerce.reglog.entity.Product;
import com.ecommerce.reglog.entity.ProductImage;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductDetailResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String brand;
    private Long categoryId;
    private String categoryName;
    private String categorySlug;
    private BigDecimal originalPrice;
    private BigDecimal sellingPrice;
    private Integer discountPercentage;
    private Integer availableStock;
    private Double rating;
    private Integer reviewsCount;
    private String gender;
    private Boolean isFeatured;
    private Instant createdAt;

    private List<String> imageUrls = new ArrayList<>();
    private List<ProductVariantDto> variants = new ArrayList<>();
    private List<String> availableColors = new ArrayList<>();
    private List<String> availableSizes = new ArrayList<>();
    private List<ProductSummaryResponse> relatedProducts = new ArrayList<>();

    public ProductDetailResponse() {}

    public static ProductDetailResponse fromEntity(Product p) {
        ProductDetailResponse res = new ProductDetailResponse();
        res.setId(p.getId());
        res.setName(p.getName());
        res.setSlug(p.getSlug());
        res.setDescription(p.getDescription());
        res.setBrand(p.getBrand());
        if (p.getCategory() != null) {
            res.setCategoryId(p.getCategory().getId());
            res.setCategoryName(p.getCategory().getName());
            res.setCategorySlug(p.getCategory().getSlug());
        }
        res.setOriginalPrice(p.getOriginalPrice());
        res.setSellingPrice(p.getSellingPrice());
        res.setDiscountPercentage(p.getDiscountPercentage());
        res.setAvailableStock(p.getAvailableStock());
        res.setRating(p.getRating());
        res.setReviewsCount(p.getReviewsCount());
        res.setGender(p.getGender() != null ? p.getGender().name() : "UNISEX");
        res.setIsFeatured(p.getIsFeatured());
        res.setCreatedAt(p.getCreatedAt());

        if (p.getImages() != null && !p.getImages().isEmpty()) {
            res.setImageUrls(p.getImages().stream()
                    .map(ProductImage::getImageUrl)
                    .collect(Collectors.toList()));
        } else if (p.getPrimaryImageUrl() != null) {
            res.getImageUrls().add(p.getPrimaryImageUrl());
        }

        if (p.getVariants() != null && !p.getVariants().isEmpty()) {
            res.setVariants(p.getVariants().stream()
                    .map(ProductVariantDto::fromEntity)
                    .collect(Collectors.toList()));

            res.setAvailableColors(p.getVariants().stream()
                    .map(v -> v.getColor())
                    .filter(c -> c != null && !c.isBlank())
                    .distinct()
                    .collect(Collectors.toList()));

            res.setAvailableSizes(p.getVariants().stream()
                    .map(v -> v.getSize())
                    .filter(s -> s != null && !s.isBlank())
                    .distinct()
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

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Boolean getIsFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(Boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<ProductVariantDto> getVariants() {
        return variants;
    }

    public void setVariants(List<ProductVariantDto> variants) {
        this.variants = variants;
    }

    public List<String> getAvailableColors() {
        return availableColors;
    }

    public void setAvailableColors(List<String> availableColors) {
        this.availableColors = availableColors;
    }

    public List<String> getAvailableSizes() {
        return availableSizes;
    }

    public void setAvailableSizes(List<String> availableSizes) {
        this.availableSizes = availableSizes;
    }

    public List<ProductSummaryResponse> getRelatedProducts() {
        return relatedProducts;
    }

    public void setRelatedProducts(List<ProductSummaryResponse> relatedProducts) {
        this.relatedProducts = relatedProducts;
    }
}
