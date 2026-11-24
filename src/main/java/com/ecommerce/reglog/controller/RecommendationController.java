package com.ecommerce.reglog.controller;

import com.ecommerce.reglog.dto.response.ApiResponse;
import com.ecommerce.reglog.dto.response.ProductSummaryResponse;
import com.ecommerce.reglog.security.UserPrincipal;
import com.ecommerce.reglog.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
@Tag(name = "Product Recommendations", description = "Endpoints for personalized, rule-based product recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/products/recommendations")
    @Operation(summary = "Get Personalized Recommendations", description = "Rule-based recommendation based on viewing history, purchases, and popularity.")
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getUserRecommendations(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "8") int limit) {
        Long userId = principal != null ? principal.getId() : null;
        List<ProductSummaryResponse> recommendations = recommendationService.getRecommendationsForUser(userId, limit);
        return ResponseEntity.ok(ApiResponse.success("Recommendations retrieved", recommendations));
    }

    @GetMapping("/products/{id}/recommendations")
    @Operation(summary = "Get Recommendations for Product", description = "Related items from the same category or brand.")
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getProductRecommendations(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "6") int limit) {
        Long userId = principal != null ? principal.getId() : null;
        List<ProductSummaryResponse> recommendations = recommendationService.getRecommendationsForProduct(id, userId, limit);
        return ResponseEntity.ok(ApiResponse.success("Related recommendations retrieved", recommendations));
    }

    @GetMapping("/cart/recommendations")
    @Operation(summary = "Get Cart Recommendations", description = "Complementary items for active cart items.")
    public ResponseEntity<ApiResponse<List<ProductSummaryResponse>>> getCartRecommendations(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "4") int limit) {
        Long userId = principal != null ? principal.getId() : null;
        List<ProductSummaryResponse> recommendations = recommendationService.getCartRecommendations(userId, limit);
        return ResponseEntity.ok(ApiResponse.success("Cart recommendations retrieved", recommendations));
    }
}
