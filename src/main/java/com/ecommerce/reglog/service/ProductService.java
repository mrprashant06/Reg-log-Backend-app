package com.ecommerce.reglog.service;

import com.ecommerce.reglog.dto.request.ProductFilterRequest;
import com.ecommerce.reglog.dto.response.FilterOptionsResponse;
import com.ecommerce.reglog.dto.response.ProductDetailResponse;
import com.ecommerce.reglog.dto.response.ProductSummaryResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {
    Page<ProductSummaryResponse> getFilteredProducts(ProductFilterRequest request, Long userId);
    ProductDetailResponse getProductById(Long id, Long userId);
    ProductDetailResponse getProductBySlug(String slug, Long userId);
    Page<ProductSummaryResponse> getProductsByCategory(Long categoryId, int page, int size);
    List<ProductSummaryResponse> getFeaturedProducts();
    List<ProductSummaryResponse> getTopRatedProducts();
    List<String> getSearchSuggestions(String query);
    FilterOptionsResponse getFilterOptions(String query, Long categoryId);
}
