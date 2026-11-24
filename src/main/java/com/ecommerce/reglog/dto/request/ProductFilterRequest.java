package com.ecommerce.reglog.dto.request;

import java.math.BigDecimal;
import java.util.List;

public class ProductFilterRequest {

    private String query;
    private Long categoryId;
    private String categorySlug;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private List<String> brands;
    private List<String> colors;
    private List<String> sizes;
    private List<Long> categoryIds;
    private String gender; // MEN, WOMEN, UNISEX, KIDS
    private String sortBy = "newest"; // "price_asc", "price_desc", "newest", "popular", "rating"
    private int page = 0;
    private int size = 12;

    public ProductFilterRequest() {}

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategorySlug() {
        return categorySlug;
    }

    public void setCategorySlug(String categorySlug) {
        this.categorySlug = categorySlug;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
    }

    public BigDecimal getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(BigDecimal maxPrice) {
        this.maxPrice = maxPrice;
    }

    public List<String> getBrands() {
        return brands;
    }

    public void setBrands(List<String> brands) {
        this.brands = brands;
    }

    public List<String> getColors() {
        return colors;
    }

    public void setColors(List<String> colors) {
        this.colors = colors;
    }

    public List<String> getSizes() {
        return sizes;
    }

    public void setSizes(List<String> sizes) {
        this.sizes = sizes;
    }

    public List<Long> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<Long> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page < 0 ? 0 : page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = (size <= 0 || size > 100) ? 12 : size;
    }
}
