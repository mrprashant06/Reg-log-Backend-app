package com.ecommerce.reglog.service.impl;

import com.ecommerce.reglog.dto.request.ProductFilterRequest;
import com.ecommerce.reglog.dto.response.*;
import com.ecommerce.reglog.entity.*;
import com.ecommerce.reglog.exception.ResourceNotFoundException;
import com.ecommerce.reglog.repository.*;
import com.ecommerce.reglog.repository.spec.ProductSpecification;
import com.ecommerce.reglog.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductVariantRepository variantRepository;
    private final UserRepository userRepository;
    private final UserSearchHistoryRepository searchHistoryRepository;
    private final RecentlyViewedProductRepository recentlyViewedRepository;

    public ProductServiceImpl(ProductRepository productRepository,
                              CategoryRepository categoryRepository,
                              ProductVariantRepository variantRepository,
                              UserRepository userRepository,
                              UserSearchHistoryRepository searchHistoryRepository,
                              RecentlyViewedProductRepository recentlyViewedRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.variantRepository = variantRepository;
        this.userRepository = userRepository;
        this.searchHistoryRepository = searchHistoryRepository;
        this.recentlyViewedRepository = recentlyViewedRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getFilteredProducts(ProductFilterRequest request, Long userId) {
        Sort sort = resolveSort(request.getSortBy());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        Specification<Product> spec = ProductSpecification.build(request);
        Page<Product> pageResult = productRepository.findAll(spec, pageable);

        // Record search history if user is searching
        if (request.getQuery() != null && !request.getQuery().trim().isEmpty() && userId != null) {
            try {
                userRepository.findById(userId).ifPresent(user -> {
                    UserSearchHistory history = new UserSearchHistory(user, request.getQuery().trim(), (int) pageResult.getTotalElements());
                    searchHistoryRepository.save(history);
                });
            } catch (Exception ignored) {}
        }

        return pageResult.map(ProductSummaryResponse::fromEntity);
    }

    @Override
    @Transactional
    public ProductDetailResponse getProductById(Long id, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));

        // Track recently viewed product
        if (userId != null) {
            try {
                userRepository.findById(userId).ifPresent(user -> {
                    RecentlyViewedProduct rvp = recentlyViewedRepository.findByUserIdAndProductId(userId, id)
                            .orElse(new RecentlyViewedProduct(user, product));
                    rvp.setViewedAt(Instant.now());
                    recentlyViewedRepository.save(rvp);
                });
            } catch (Exception ignored) {}
        }

        ProductDetailResponse response = ProductDetailResponse.fromEntity(product);

        // Add related products from same category
        if (product.getCategory() != null) {
            List<Product> related = productRepository.findTop8ByCategoryIdAndIdNotAndIsActiveTrue(
                    product.getCategory().getId(), product.getId());
            response.setRelatedProducts(related.stream()
                    .map(ProductSummaryResponse::fromEntity)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    @Override
    @Transactional
    public ProductDetailResponse getProductBySlug(String slug, Long userId) {
        Product product = productRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "slug", slug));

        return getProductById(product.getId(), userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductSummaryResponse> getProductsByCategory(Long categoryId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId, pageable)
                .map(ProductSummaryResponse::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getFeaturedProducts() {
        return productRepository.findTop8ByIsFeaturedTrueAndIsActiveTrueOrderByCreatedAtDesc().stream()
                .map(ProductSummaryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductSummaryResponse> getTopRatedProducts() {
        return productRepository.findTop8ByIsActiveTrueOrderByRatingDesc().stream()
                .map(ProductSummaryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getSearchSuggestions(String query) {
        if (query == null || query.trim().length() < 2) {
            return Collections.emptyList();
        }
        List<Product> matches = productRepository.findTop10Suggestions(query.trim());
        Set<String> suggestions = new LinkedHashSet<>();
        for (Product p : matches) {
            suggestions.add(p.getName());
            suggestions.add(p.getBrand());
        }
        return new ArrayList<>(suggestions);
    }

    @Override
    @Transactional(readOnly = true)
    public FilterOptionsResponse getFilterOptions(String query, Long categoryId) {
        FilterOptionsResponse response = new FilterOptionsResponse();

        // All active categories
        List<CategoryResponse> categories = categoryRepository.findByIsActiveTrueOrderByDisplayOrderAsc().stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
        response.setCategories(categories);

        // Distinct brands
        response.setBrands(productRepository.findDistinctBrands());

        // Standard filter values
        response.setGenders(Arrays.asList("Men", "Women", "Unisex", "Kids"));
        response.setColors(Arrays.asList("Black", "White", "Blue", "Red", "Green", "Grey", "Brown", "Gold", "Pink"));
        response.setSizes(Arrays.asList("S", "M", "L", "XL", "XXL", "UK 7", "UK 8", "UK 9", "UK 10", "Free Size"));

        // Price range
        List<Product> allProducts = productRepository.findAll();
        BigDecimal min = allProducts.stream()
                .map(Product::getSellingPrice)
                .filter(Objects::nonNull)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.valueOf(199));

        BigDecimal max = allProducts.stream()
                .map(Product::getSellingPrice)
                .filter(Objects::nonNull)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.valueOf(99999));

        response.setMinPrice(min);
        response.setMaxPrice(max);

        return response;
    }

    private Sort resolveSort(String sortBy) {
        if (sortBy == null) return Sort.by(Sort.Direction.DESC, "createdAt");
        switch (sortBy.toLowerCase().trim()) {
            case "price_asc":
            case "price_low":
                return Sort.by(Sort.Direction.ASC, "sellingPrice");
            case "price_desc":
            case "price_high":
                return Sort.by(Sort.Direction.DESC, "sellingPrice");
            case "popular":
                return Sort.by(Sort.Direction.DESC, "reviewsCount", "rating");
            case "rating":
                return Sort.by(Sort.Direction.DESC, "rating");
            case "newest":
            default:
                return Sort.by(Sort.Direction.DESC, "createdAt");
        }
    }
}
