package com.ecommerce.reglog.dto.response;

import com.ecommerce.reglog.entity.WishlistItem;
import java.time.Instant;

public class WishlistItemResponse {

    private Long id;
    private ProductSummaryResponse product;
    private Instant createdAt;

    public WishlistItemResponse() {}

    public static WishlistItemResponse fromEntity(WishlistItem item) {
        WishlistItemResponse res = new WishlistItemResponse();
        res.setId(item.getId());
        if (item.getProduct() != null) {
            res.setProduct(ProductSummaryResponse.fromEntity(item.getProduct()));
        }
        res.setCreatedAt(item.getCreatedAt());
        return res;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ProductSummaryResponse getProduct() {
        return product;
    }

    public void setProduct(ProductSummaryResponse product) {
        this.product = product;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
