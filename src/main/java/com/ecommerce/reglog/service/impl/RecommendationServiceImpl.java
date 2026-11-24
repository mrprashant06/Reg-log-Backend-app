package com.ecommerce.reglog.service.impl;

import com.ecommerce.reglog.dto.response.ProductSummaryResponse;
import com.ecommerce.reglog.entity.*;
import com.ecommerce.reglog.repository.*;
import com.ecommerce.reglog.service.RecommendationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final ProductRepository productRepository;
    private final RecentlyViewedProductRepository recentlyViewedRepository;
    private final UserSearchHistoryRepository searchHistoryRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;

    public RecommendationServiceImpl(ProductRepository productRepository,
                                     RecentlyViewedProductRepository recentlyViewedRepository,
                                     UserSearchHistoryRepository searchHistoryRepository,
                                     OrderRepository orderRepository,
                                     CartRepository cartRepository) {
        this.productRepository = productRepository;
        this.recentlyViewedRepository = recentlyViewedRepository;
        this.searchHistoryRepository = searchHistoryRepository;
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getRecommendationsForUser(Long userId, int limit) {
        Set<Product> recommended = new LinkedHashSet<>();
        Set<Long> excludedProductIds = new HashSet<>();

        if (userId != null) {
            // 1. Get recently viewed products
            List<RecentlyViewedProduct> recentViews = recentlyViewedRepository.findTop10ByUserIdOrderByViewedAtDesc(userId);
            for (RecentlyViewedProduct rvp : recentViews) {
                excludedProductIds.add(rvp.getProduct().getId());
            }

            // 2. Recommend from same categories and brands as recent views
            for (RecentlyViewedProduct rvp : recentViews) {
                Product p = rvp.getProduct();
                if (p.getCategory() != null) {
                    List<Product> sameCat = productRepository.findTop8ByCategoryIdAndIdNotAndIsActiveTrue(
                            p.getCategory().getId(), p.getId());
                    for (Product cp : sameCat) {
                        if (!excludedProductIds.contains(cp.getId())) {
                            recommended.add(cp);
                        }
                    }
                }
                if (recommended.size() >= limit) break;
            }

            // 3. If still under limit, recommend based on past purchases
            if (recommended.size() < limit) {
                List<PurchaseOrder> orders = orderRepository.findAll((root, query, cb) ->
                        cb.equal(root.get("user").get("id"), userId));
                for (PurchaseOrder order : orders) {
                    for (OrderItem item : order.getItems()) {
                        if (item.getProduct() != null && item.getProduct().getCategory() != null) {
                            List<Product> sameCat = productRepository.findTop8ByCategoryIdAndIdNotAndIsActiveTrue(
                                    item.getProduct().getCategory().getId(), item.getProduct().getId());
                            for (Product sp : sameCat) {
                                if (!excludedProductIds.contains(sp.getId())) {
                                    recommended.add(sp);
                                }
                            }
                        }
                    }
                    if (recommended.size() >= limit) break;
                }
            }
        }

        // 4. Fallback to featured and top-rated popular products
        if (recommended.size() < limit) {
            List<Product> featured = productRepository.findTop8ByIsFeaturedTrueAndIsActiveTrueOrderByCreatedAtDesc();
            for (Product fp : featured) {
                if (!excludedProductIds.contains(fp.getId())) {
                    recommended.add(fp);
                }
                if (recommended.size() >= limit) break;
            }
        }

        if (recommended.size() < limit) {
            List<Product> topRated = productRepository.findTop8ByIsActiveTrueOrderByRatingDesc();
            for (Product tp : topRated) {
                if (!excludedProductIds.contains(tp.getId())) {
                    recommended.add(tp);
                }
                if (recommended.size() >= limit) break;
            }
        }

        return recommended.stream()
                .limit(limit)
                .map(ProductSummaryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getRecommendationsForProduct(Long productId, Long userId, int limit) {
        Set<Product> result = new LinkedHashSet<>();
        productRepository.findById(productId).ifPresent(p -> {
            // Same category
            if (p.getCategory() != null) {
                result.addAll(productRepository.findTop8ByCategoryIdAndIdNotAndIsActiveTrue(
                        p.getCategory().getId(), p.getId()));
            }
            // Same brand
            if (result.size() < limit && p.getBrand() != null) {
                result.addAll(productRepository.findTop8ByBrandIgnoreCaseAndIdNotAndIsActiveTrue(
                        p.getBrand(), p.getId()));
            }
        });

        if (result.size() < limit) {
            result.addAll(productRepository.findTop8ByIsFeaturedTrueAndIsActiveTrueOrderByCreatedAtDesc());
        }

        return result.stream()
                .filter(p -> !p.getId().equals(productId))
                .limit(limit)
                .map(ProductSummaryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getCartRecommendations(Long userId, int limit) {
        Set<Product> result = new LinkedHashSet<>();
        Set<Long> inCartProductIds = new HashSet<>();

        if (userId != null) {
            cartRepository.findByUserId(userId).ifPresent(cart -> {
                for (CartItem item : cart.getItems()) {
                    if (item.getProduct() != null) {
                        inCartProductIds.add(item.getProduct().getId());
                    }
                }
                // Recommend complementary items
                for (CartItem item : cart.getItems()) {
                    if (item.getProduct() != null && item.getProduct().getCategory() != null) {
                        List<Product> sameCat = productRepository.findTop8ByCategoryIdAndIdNotAndIsActiveTrue(
                                item.getProduct().getCategory().getId(), item.getProduct().getId());
                        for (Product cp : sameCat) {
                            if (!inCartProductIds.contains(cp.getId())) {
                                result.add(cp);
                            }
                        }
                    }
                    if (result.size() >= limit) break;
                }
            });
        }

        if (result.size() < limit) {
            List<Product> topRated = productRepository.findTop8ByIsActiveTrueOrderByRatingDesc();
            for (Product tp : topRated) {
                if (!inCartProductIds.contains(tp.getId())) {
                    result.add(tp);
                }
                if (result.size() >= limit) break;
            }
        }

        return result.stream()
                .limit(limit)
                .map(ProductSummaryResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
