package com.ecommerce.reglog.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "recently_viewed_products", indexes = {
    @Index(name = "idx_recent_user", columnList = "user_id"),
    @Index(name = "idx_recent_viewed", columnList = "viewed_at")
})
public class RecentlyViewedProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "viewed_at", nullable = false)
    private Instant viewedAt = Instant.now();

    public RecentlyViewedProduct() {}

    public RecentlyViewedProduct(User user, Product product) {
        this.user = user;
        this.product = product;
        this.viewedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Instant getViewedAt() {
        return viewedAt;
    }

    public void setViewedAt(Instant viewedAt) {
        this.viewedAt = viewedAt;
    }
}
