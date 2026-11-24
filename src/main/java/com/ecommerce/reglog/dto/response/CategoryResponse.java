package com.ecommerce.reglog.dto.response;

import com.ecommerce.reglog.entity.Category;

public class CategoryResponse {

    private Long id;
    private String name;
    private String slug;
    private String description;
    private String imageUrl;
    private Integer displayOrder;
    private Boolean isActive;
    private long productCount;

    public CategoryResponse() {}

    public static CategoryResponse fromEntity(Category category) {
        CategoryResponse res = new CategoryResponse();
        res.setId(category.getId());
        res.setName(category.getName());
        res.setSlug(category.getSlug());
        res.setDescription(category.getDescription());
        res.setImageUrl(category.getImageUrl());
        res.setDisplayOrder(category.getDisplayOrder());
        res.setIsActive(category.getIsActive());
        res.setProductCount(category.getProducts() != null ? category.getProducts().size() : 0);
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public long getProductCount() {
        return productCount;
    }

    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }
}
