package com.ecommerce.reglog.service;

import com.ecommerce.reglog.dto.response.ProductSummaryResponse;
import java.util.List;

public interface RecommendationService {
    List<ProductSummaryResponse> getRecommendationsForUser(Long userId, int limit);
    List<ProductSummaryResponse> getRecommendationsForProduct(Long productId, Long userId, int limit);
    List<ProductSummaryResponse> getCartRecommendations(Long userId, int limit);
}
