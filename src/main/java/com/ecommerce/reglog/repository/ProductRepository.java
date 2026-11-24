package com.ecommerce.reglog.repository;

import com.ecommerce.reglog.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    Optional<Product> findBySlug(String slug);

    Page<Product> findByCategoryIdAndIsActiveTrue(Long categoryId, Pageable pageable);

    Page<Product> findByCategorySlugAndIsActiveTrue(String slug, Pageable pageable);

    List<Product> findTop8ByIsFeaturedTrueAndIsActiveTrueOrderByCreatedAtDesc();

    List<Product> findTop8ByIsActiveTrueOrderByRatingDesc();

    List<Product> findTop8ByCategoryIdAndIdNotAndIsActiveTrue(Long categoryId, Long productId);

    List<Product> findTop8ByBrandIgnoreCaseAndIdNotAndIsActiveTrue(String brand, Long productId);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.isActive = true ORDER BY p.brand ASC")
    List<String> findDistinctBrands();

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Product> findTop10Suggestions(String query);
}
