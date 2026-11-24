package com.ecommerce.reglog.repository;

import com.ecommerce.reglog.entity.RecentlyViewedProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecentlyViewedProductRepository extends JpaRepository<RecentlyViewedProduct, Long> {
    List<RecentlyViewedProduct> findTop10ByUserIdOrderByViewedAtDesc(Long userId);
    Optional<RecentlyViewedProduct> findByUserIdAndProductId(Long userId, Long productId);
}
